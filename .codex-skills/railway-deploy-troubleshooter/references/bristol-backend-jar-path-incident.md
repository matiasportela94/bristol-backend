# Bristol Backend Railway Incident

## Scenario

- Project: `lively-smile`
- Service: `bristol-backend`
- Date: `2026-04-06`
- Primary error:

```text
Starting Container
Error: Unable to access jarfile bristol-api/target/bristol-api-3.0.0.jar
```

## What Did Not Work

1. Assuming the Railway runtime error was only a missing build artifact.
The repo `Dockerfile` was already wrong: it copied `bristol-application/target/*.jar` into the runtime image even though the bootable Spring Boot jar is built by `bristol-api`.

2. Trying to inspect Railway state from the sandbox without escalation.
`railway status`, `railway list`, and `railway whoami` returned unauthorized inside the sandbox because the Railway auth session was only visible outside it.

3. Attempting `railway login -b` from this non-interactive environment.
That failed with `Cannot login in non-interactive mode`.

4. Redeploying after only fixing the `Dockerfile`.
The build succeeded, but the deployment still crashed with the same old path:

```text
Error: Unable to access jarfile bristol-api/target/bristol-api-3.0.0.jar
```

This proved Railway was not using the image entrypoint and was instead applying a stale custom Start Command from service settings.

5. Trying to clear the override via `railway.toml` using `startCommand = null`.
That redeploy failed before a build attached. TOML does not support JSON `null`, so this was the wrong config format for the override.

6. Treating a `railway up` timeout as a failed upload.
One redeploy returned a GraphQL timeout, but `railway deployment list` showed the deployment was still registered and progressing. Always inspect deployment state before retrying.

## Final Working Solution

1. Fix the runtime image to copy the executable jar from `bristol-api`.

```dockerfile
COPY --from=build /app/bristol-api/target/bristol-api-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

2. Add `railway.json` at the repo root to force Dockerfile-based deployment behavior and clear the stale Start Command override.

```json
{
  "$schema": "https://railway.com/railway.schema.json",
  "build": {
    "builder": "DOCKERFILE",
    "dockerfilePath": "Dockerfile"
  },
  "deploy": {
    "startCommand": null
  }
}
```

3. Redeploy with Railway CLI after linking the repo to:
- Project: `lively-smile`
- Environment: `production`
- Service: `bristol-backend`

4. Verify success from runtime logs, not only deployment status.
The successful deployment no longer referenced `bristol-api/target/bristol-api-3.0.0.jar` and started from:

```text
/app/app.jar
```

## Successful Signals

- `railway deployment list` showed deployment `ee9f8b1c-fd8e-4455-a5ef-ae6e89891734` as `SUCCESS`.
- Runtime logs showed:

```text
Starting BristolApplication using Java 17.0.18 with PID 1 (/app/app.jar started by root in /app)
```

## Remaining Non-Blocking Warnings

- Flyway warned PostgreSQL `18.3` is newer than the latest tested version for the bundled Flyway.
- Hibernate warned that explicit `hibernate.dialect` is unnecessary.
- Spring warned that `spring.jpa.open-in-view` is enabled by default.
