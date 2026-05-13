package com.bristol.domain.user;

/**
 * User role enumeration.
 * Matches the user_role ENUM in the database schema.
 */
public enum UserRole {
    ADMIN,
    USER,
    /** Main user of a distributor — sees all branches of their distributor. */
    DISTRIBUTOR,
    /** User linked to a specific distributor branch — sees only their branch's data. */
    DISTRIBUTOR_BRANCH
}
