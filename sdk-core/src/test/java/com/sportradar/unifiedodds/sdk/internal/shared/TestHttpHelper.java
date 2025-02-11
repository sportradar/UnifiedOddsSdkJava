package com.sportradar.unifiedodds.sdk.internal.shared;

import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.UserAgentProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.MessageAndActionExtractor;
import java.util.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpStatus;

@SuppressWarnings(
    {
        "DeclarationOrder",
        "LineLength",
        "MemberName",
        "MissingSwitchDefault",
        "MultipleStringLiterals",
        "VisibilityModifier",
    }
)
public class TestHttpHelper extends HttpHelper {

    /**
     * The list of URI replacements (to get wanted response when specific url is called)
     */
    public Map<String, String> UriReplacements;

    /**
     * The list of possible post responses (to get wanted response when specific url is called)
     * string: url (or part of it) to be searched, int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
     */
    public List<UrlReplacement> PostResponses;

    /**
     * The list of possible put responses (to get wanted response when specific url is called)
     * string: url (or part of it) to be searched, int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
     */
    public List<UrlReplacement> PutResponses;

    /**
     * The list of possible delete responses (to get wanted response when specific url is called)
     * string: url (or part of it) to be searched, int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
     */
    public List<UrlReplacement> DeleteResponses;

    /**
     * The list of URI exceptions (to get wanted response when specific url is called)
     */
    public Map<String, CommunicationException> UriExceptions;

    /**
     * The list of called urls
     */
    public List<String> CalledUrls;

    @Inject
    public TestHttpHelper(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        MessageAndActionExtractor messageExtractor,
        UserAgentProvider userAgentProvider
    ) {
        super(config, httpClient, messageExtractor, userAgentProvider);
        UriReplacements = new HashMap<>();
        PostResponses = new ArrayList<>();
        PutResponses = new ArrayList<>();
        DeleteResponses = new ArrayList<>();
        UriExceptions = new HashMap<>();
        CalledUrls = new ArrayList<>();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public ResponseData post(String path) throws CommunicationException {
        CalledUrls.add(path);
        getUriException(path);
        ResponseData response = new ResponseData(HttpStatus.SC_NOT_ACCEPTABLE, "default");
        Optional<UrlReplacement> urlReplacement = PostResponses
            .stream()
            .filter(f -> path.contains(f.getUrl()))
            .findFirst();
        if (urlReplacement.isPresent()) {
            switch (urlReplacement.get().responseType) {
                case 0:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_BAD_REQUEST, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 1:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_ACCEPTED, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 2:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_NOT_FOUND, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 3:
                    throw new CommunicationException("Cant access path", path, HttpStatus.SC_GATEWAY_TIMEOUT);
            }
        }

        return response;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public ResponseData put(String path) throws CommunicationException {
        CalledUrls.add(path);
        getUriException(path);
        ResponseData response = new ResponseData(HttpStatus.SC_NOT_ACCEPTABLE, "default");
        Optional<UrlReplacement> urlReplacement = PutResponses
            .stream()
            .filter(f -> path.contains(f.getUrl()))
            .findFirst();
        if (urlReplacement.isPresent()) {
            switch (urlReplacement.get().responseType) {
                case 0:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_BAD_REQUEST, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 1:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_ACCEPTED, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 2:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_NOT_FOUND, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 3:
                    throw new CommunicationException("Cant access path", path, HttpStatus.SC_GATEWAY_TIMEOUT);
            }
        }

        return response;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public ResponseData delete(String path) throws CommunicationException {
        CalledUrls.add(path);
        getUriException(path);
        ResponseData response = new ResponseData(HttpStatus.SC_NOT_ACCEPTABLE, "default");
        Optional<UrlReplacement> urlReplacement = DeleteResponses
            .stream()
            .filter(f -> path.contains(f.getUrl()))
            .findFirst();
        if (urlReplacement.isPresent()) {
            switch (urlReplacement.get().responseType) {
                case 0:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_BAD_REQUEST, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 1:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_ACCEPTED, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 2:
                    response =
                        urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_NOT_FOUND, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 3:
                    throw new CommunicationException("Cant access path", path, HttpStatus.SC_GATEWAY_TIMEOUT);
            }
        }

        return response;
    }

    private String getPathWithReplacements(String path) {
        if (UriReplacements == null || UriReplacements.isEmpty()) {
            return path;
        }
        Optional<String> replacement = UriReplacements.keySet().stream().filter(path::contains).findFirst();
        if (replacement.isPresent()) {
            return UriReplacements.get(replacement.get());
        }
        return path;
    }

    private void getUriException(String path) throws CommunicationException {
        if (UriExceptions == null || UriExceptions.isEmpty()) {
            return;
        }
        Optional<String> exceptionKey = UriExceptions.keySet().stream().filter(path::contains).findFirst();
        if (exceptionKey.isPresent()) {
            throw UriExceptions.get(exceptionKey.get());
        }
    }

    public static class UrlReplacement {

        private final String urlPart;
        private final int responseType;
        private final int httpStatus;

        /**
         * Get the url part to be checked within request
         * @return the url part to be checked within request
         */
        public String getUrl() {
            return urlPart;
        }

        /**
         * Get the response type
         * @return the response type
         */
        public int getResponseType() {
            return responseType;
        }

        /**
         * Get wanted http response
         * @return wanted http response
         */
        public int getHttpStatus() {
            return httpStatus;
        }

        /**
         * Construct new instance
         * @param url url (or part of it) to be searched
         * @param responseType int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
         * @param httpStatus wanted response
         */
        public UrlReplacement(String url, int responseType, int httpStatus) {
            this.urlPart = url;
            this.responseType = responseType;
            this.httpStatus = httpStatus;
        }
    }
}
