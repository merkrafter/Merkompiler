package com.merkrafter;

/**
 * This enum defines all error codes that this program can exit with.
 *
 * @author merkrafter
 */
public enum ErrorCode {
    NOT_ENOUGH_ARGUMENTS(1),
    FILE_NOT_FOUND(2);

    public final int id;

    ErrorCode(final int id) {
        this.id = id;
    }
}
