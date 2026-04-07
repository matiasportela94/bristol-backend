---
name: railway-deploy-troubleshooter
description: Diagnose and fix Railway deployment failures for containerized apps, especially stale custom Start Commands, Dockerfile/runtime mismatches, broken jar paths, failed redeploys, and cases where Railway ignores the container entrypoint. Use when Codex needs to inspect Railway CLI state, deploy logs, service linkage, config-as-code files, or preserve a repeatable incident record.
---

# Railway Deploy Troubleshooter

Use this skill to debug a Railway deployment end to end, record failed attempts, and preserve the working fix path for reuse.

## Workflow

1. Inspect local build and runtime definitions before touching Railway.
Check `Dockerfile`, `railway.json` or `railway.toml`, app build files, and the expected runtime artifact path.

2. Compare the deployment error with the image contents.
If Railway logs show `Unable to access jarfile ...`, verify whether the image actually copies that jar into the runtime image. For multi-module Maven projects, confirm which module produces the bootable jar.

3. Verify Railway CLI auth and link state.
Run `railway whoami`, `railway status`, `railway list`, and `railway link` as needed. If Railway auth works outside the sandbox but not inside it, use escalated Railway CLI commands rather than assuming the user is not logged in.

4. Deploy the minimal repo-side fix first.
If the container image is wrong, fix the image definition and redeploy once before changing service configuration.

5. If Railway still starts the old command, assume a stale custom Start Command override.
When logs continue to show an outdated command even after a successful image build, Railway is likely overriding the container entrypoint from service settings.

6. Override service config with config-as-code.
Prefer `railway.json` at the repo root for deterministic deployment behavior. For Docker deployments that should use the image entrypoint, set:

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

7. Redeploy and verify runtime logs, not just deployment status.
Look for the real executable path in logs, such as `/app/app.jar`, and confirm the service no longer references the stale jar path.

## Validation Checklist

- Confirm the linked project, environment, and service are correct.
- Confirm the latest deployment is `SUCCESS`, not merely `DEPLOYING`.
- Confirm runtime logs show the expected startup command or artifact path.
- Record warnings separately from blockers.
- Save dead ends and rejected fixes in a reference file for future reuse.

## Common Failure Patterns

- Dockerfile copies a non-executable module artifact instead of the bootable jar.
- Railway service has a stale custom Start Command that overrides the container entrypoint.
- CLI appears logged out inside the sandbox because Railway auth files live outside it.
- `railway.toml` is used with JSON-style `null`; prefer `railway.json` for `startCommand: null`.
- `railway up` times out on log streaming even though the deployment was accepted; check `railway deployment list` before retrying.

## References

- For a concrete incident record with failed attempts and the final fix, read [references/bristol-backend-jar-path-incident.md](references/bristol-backend-jar-path-incident.md).
