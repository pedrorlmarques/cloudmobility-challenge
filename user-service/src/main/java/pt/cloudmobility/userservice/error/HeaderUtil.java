package pt.cloudmobility.userservice.error;

import org.springframework.http.HttpHeaders;

public final class HeaderUtil {

    private HeaderUtil() {
        //private constructor
    }

    public static HttpHeaders createFailureAlert(String applicationName, boolean enableTranslation, String entityName,
                                                 String errorKey, String defaultMessage) {
        String message = enableTranslation ? "error." + errorKey : defaultMessage;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-error", message);
        headers.add("X-" + applicationName + "-params", entityName);
        return headers;
    }
}
