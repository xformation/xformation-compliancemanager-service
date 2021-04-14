/*
 * */
package com.synectiks.process.common.freeenterprise;

public class FreeLicenseRequestException extends RuntimeException {
    private final FreeLicenseRequest request;

    public FreeLicenseRequestException(String message, FreeLicenseRequest request, Throwable e) {
        super(message, e);
        this.request = request;
    }

    public FreeLicenseRequestException(String message, FreeLicenseRequest request) {
        super(message);
        this.request = request;
    }

    public FreeLicenseRequest getRequest() {
        return request;
    }
}
