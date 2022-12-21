package nl.tudelft.sem.template.association.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestUtil {

    private final transient ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private transient RestTemplate restTemplate;

    @Bean
    public RestTemplate generateTestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    private static final String AUTH_HEADER = "Authorization";

    /**
     * Builds the server address for you.
     *
     * @param port The port of the server
     * @param parameters The parameters of the server, exampele "test/someEndpoint"
     * @return The server address
     */
    private String giveServerAddress(int port, String parameters) {
        return "http://localhost:" + port + "/" + parameters;
    }

    /**
     * Converts an HttpServletRequest to a modeltype that you provide.
     *
     * @param request The request you want to convert to the model,
     *                This request should contain the data of that model
     * @param typeClass The type of model you want to convert to
     * @param <M> The actual class of that model
     * @return The model converted from the request.
     * @throws IOException If the request does not contain the correct information for the model,
     *                     this method might give an IOException
     */
    public <M> M convertToModel(HttpServletRequest request, Class<M> typeClass) throws IOException {
        return mapper.readValue(request.getInputStream(), typeClass);
    }

    /**
     * Gets the Authorization token out of the HttpServletRequest.
     *
     * @param request The request that the token should be extracted from
     * @return The token that has been extracted
     */
    public String getToken(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER).split(" ")[1];
    }

    /**
     * This method allows for a generic post request to another microservice.
     *
     * <p>To use this method the endpoint that started the execution chain must have an HttpServletRequest as parameter.
     * This is due to the fact that we need the token out of the request,
     * and we do not get this if that function parameter was a model.</p>
     *
     * <p>Use the function convertToModel, to still get the model from the HttpServletRequest.
     * Use the function getToken, to get the authentication token from the HttpServletRequest.</p>
     *
     * @param request The data that should be given to the other microservice
     * @param responseClass The class of the response that will be given
     * @param token The token that the user has provided to authorize themselves
     * @param port The port of the other microservice
     * @param parameters The parameters of the other microservice, example: "test/endpoint"
     * @param <M> The type of model that is used for the request (requestModel/modeltype)
     * @param <R> The type of response that is given
     * @return The response of the post request from the other microservice
     */
    public <M, R> ResponseEntity<R> postRequest(M request, Class<R> responseClass,
                                                String token, int port, String parameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(AUTH_HEADER, "Bearer " + token);
        HttpEntity<M> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(giveServerAddress(port, parameters), entity, responseClass);
    }

    /**
     * This method allows for a generic get request to another microservice.
     * This overload does not need a body
     *
     * <p>To use this method the endpoint that started the execution chain must have an HttpServletRequest as parameter.
     * This is due to the fact that we need the token out of the request,
     * and we do not get this if that function parameter was a model.</p>
     *
     * <p>Use the function convertToModel, to still get the model from the HttpServletRequest.
     * Use the function getToken, to get the authentication token from the HttpServletRequest.</p>
     *
     * @param responseClass The class of the response that will be given
     * @param token The token that the user has provided to authorize themselves
     * @param port The port of the other microservice
     * @param parameters The parameters of the other microservice, example: "test/endpoint"
     * @param <R> the type of response that is given
     * @return The response of the get request from the other microservice
     */
    public <R> ResponseEntity<R> getRequest(Class<R> responseClass,
                                            String token, int port, String parameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(AUTH_HEADER, "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(giveServerAddress(port, parameters), HttpMethod.GET, entity, responseClass);
    }

    /**
     * This method allows for a generic get request to another microservice.
     * This overload also allows to change the body of the get request with a specific model.
     *
     * <p>To use this method the endpoint that started the execution chain must have an HttpServletRequest as parameter.
     * This is due to the fact that we need the token out of the request,
     * and we do not get this if that function parameter was a model.</p>
     *
     * <p>Use the function convertToModel, to still get the model from the HttpServletRequest.
     * Use the function getToken, to get the authentication token from the HttpServletRequest.</p>
     *
     * @param request The data that should be given to the other microservice
     * @param responseClass The class of the response that will be given
     * @param token The token that the user has provided to authorize themselves
     * @param port The port of the other microservice
     * @param parameters The parameters of the other microservice, example: "test/endpoint"
     * @param <M> The type of model that is used for the request (requestModel/modeltype)
     * @param <R> the type of response that is given
     * @return The response of the get request from the other microservice
     */
    public <M, R> ResponseEntity<R> getRequest(M request, Class<R> responseClass,
                                            String token, int port, String parameters) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(AUTH_HEADER, "Bearer " + token);
        HttpEntity<M> entity = new HttpEntity<>(request, headers);

        return restTemplate.exchange(giveServerAddress(port, parameters), HttpMethod.GET, entity, responseClass);
    }

}
