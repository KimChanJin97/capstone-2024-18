package capstone.facefriend.auth.interceptor;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    HEAD,
    TRACE,
    CONNECT,
    ANY,
    ;

    public boolean matches(String pathMethod) {
        return this == ANY || this.name().equalsIgnoreCase(pathMethod);
    }
}

