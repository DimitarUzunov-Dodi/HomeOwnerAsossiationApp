package nl.tudelft.sem.template.association.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.StringLength;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class RequestUtilTest {

    @InjectMocks
    private RequestUtil requestUtil = new RequestUtil();

    @Mock
    private RestTemplate restTemplate;

    public <M, R> void genericTestPostRequest(M data, Class<R> responseClass, R responseData,
                                              String token, int port, String parameters, String expectedServerAddress) {
        ArgumentCaptor<HttpEntity<M>> captureEntity = ArgumentCaptor.forClass(HttpEntity.class);

        doReturn(ResponseEntity.ok(responseData)).when(restTemplate)
                .postForEntity(eq(expectedServerAddress), any(HttpEntity.class), eq(responseClass));

        assertThat(requestUtil.postRequest(data, responseClass, token, port, parameters))
                .isEqualTo(ResponseEntity.ok(responseData));

        verify(restTemplate, times(1))
                .postForEntity(eq(expectedServerAddress), captureEntity.capture(), eq(responseClass));

        HttpEntity<M> entity = captureEntity.getValue();

        assertThat(entity.getHeaders().containsKey("Authorization")).isTrue();
        assertThat(entity.getHeaders().get("Authorization")).containsExactly(("Bearer " + token));

        assertThat(entity.getBody()).isEqualTo(data);
    }

    @Test
    public void postRequestIntTest() {
        TestModel<Integer> model = new TestModel<>();
        model.someData = 28402;
        genericTestPostRequest(model, Integer.class, 9439, "token", 8080, "test/somepath", "http://localhost:8080/test/somepath");
    }

    @Test
    public void postRequestStringTest() {
        TestModel<String> model = new TestModel<>();
        model.someData = "thisIS some kind of a string10(&^$@";
        genericTestPostRequest(model, String.class, "test return data", "token", 8080, "test/somepathString", "http://localhost:8080/test/somepathString");
    }

    @Test
    public void postRequestDoubleTest() {
        TestModel<Double> model = new TestModel<>();
        model.someData = 28402.651954;
        genericTestPostRequest(model, Double.class, 29829.20490, "token", 8080, "testdouble/somepath", "http://localhost:8080/testdouble/somepath");
    }

    public <R> void genericTestGetRequest(Class<R> responseClass, R responseData,
                                          String token, int port, String parameters, String expectedServerAddress) {
        ArgumentCaptor<HttpEntity<Object>> captureEntity = ArgumentCaptor.forClass(HttpEntity.class);

        doReturn(ResponseEntity.ok(responseData)).when(restTemplate)
                .exchange(eq(expectedServerAddress), eq(HttpMethod.GET), any(HttpEntity.class), eq(responseClass));

        assertThat(requestUtil.getRequest(responseClass, token, port, parameters))
                .isEqualTo(ResponseEntity.ok(responseData));

        verify(restTemplate, times(1))
                .exchange(eq(expectedServerAddress), eq(HttpMethod.GET), captureEntity.capture(), eq(responseClass));

        HttpEntity<Object> entity = captureEntity.getValue();

        assertThat(entity.getHeaders().containsKey("Authorization")).isTrue();
        assertThat(entity.getHeaders().get("Authorization")).containsExactly(("Bearer " + token));

        assertThat(entity.getBody()).isEqualTo(null);
    }

    public <M, R> void genericTestGetRequest(M data, Class<R> responseClass, R responseData,
                                             String token, int port, String parameters, String expectedServerAddress) {
        ArgumentCaptor<HttpEntity<M>> captureEntity = ArgumentCaptor.forClass(HttpEntity.class);

        doReturn(ResponseEntity.ok(responseData)).when(restTemplate)
                .exchange(eq(expectedServerAddress), eq(HttpMethod.GET), any(HttpEntity.class), eq(responseClass));

        assertThat(requestUtil.getRequest(data, responseClass, token, port, parameters))
                .isEqualTo(ResponseEntity.ok(responseData));

        verify(restTemplate, times(2))
                .exchange(eq(expectedServerAddress), eq(HttpMethod.GET), captureEntity.capture(), eq(responseClass));

        HttpEntity<M> entity = captureEntity.getValue();

        assertThat(entity.getHeaders().containsKey("Authorization")).isTrue();
        assertThat(entity.getHeaders().get("Authorization")).containsExactly(("Bearer " + token));

        assertThat(entity.getBody()).isEqualTo(data);
    }

    @Test
    public void getRequestIntTest() {
        genericTestGetRequest(Integer.class, 295839, "token", 8080, "some/endpoint", "http://localhost:8080/some/endpoint");
        genericTestGetRequest(3849384, Integer.class, 295839, "token", 8080, "some/endpoint", "http://localhost:8080/some/endpoint");
    }

    @Test
    public void getRequesStringTest() {
        genericTestGetRequest(String.class, "Some weird string29835390852\":?>';][.}{>}{<>}(*&90&*&^%$'", "other Token", 8085, "some/endpoint", "http://localhost:8085/some/endpoint");
        genericTestGetRequest("someDataString289393482::}>?>+_)(*&^%#@", String.class, "Some weird string29835390852\":?>';][.}{>}{<>}(*&90&*&^%$'", "other Token", 8085, "some/endpoint", "http://localhost:8085/some/endpoint");
    }

    @Test
    public void getRequestDoubleTest() {
        genericTestGetRequest(Double.class, 295839.48304839, "token", 8082, "some/endpoint\":?>/;''][';/..\":\"}{*&__++_+_']", "http://localhost:8082/some/endpoint\":?>/;''][';/..\":\"}{*&__++_+_']");
        genericTestGetRequest(39530.903940, Double.class, 295839.48304839, "token", 8082, "some/endpoint\":?>/;''][';/..\":\"}{*&__++_+_']", "http://localhost:8082/some/endpoint\":?>/;''][';/..\":\"}{*&__++_+_']");
    }

    public <M> void genericTestConvertToModel(M data, Class<M> typeClass) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);

            ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

            HttpStream stream = new HttpStream(bis);

            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getInputStream()).thenReturn(stream);

            assertThat(requestUtil.convertToModel(request, typeClass)).isEqualTo(data);
        } catch (IOException e) {
            fail("IOException in test case, here is the error: " + e.getMessage());
        }
    }

    @Test
    public void testConvertToModelInt() {
        genericTestConvertToModel(29483, Integer.class);
    }

    @Test
    public void testConvertToModelString() {
        genericTestConvertToModel("this is a weird test string289478920835@#$&*%}\">::~\"\"})^()_*&%~~", String.class);
    }

    @Test
    public void testConvertToModelDouble() {
        genericTestConvertToModel(29483.564825555, Double.class);
    }

    public void genericTestGetToken(String token) {
        if (token.contains(" ")) {
            return;
        }

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        assertThat(requestUtil.getToken(request)).isEqualTo(token);
    }

    @Property
    public void testGetToken(@ForAll @StringLength(min = 1) String string) {
        genericTestGetToken(string);
    }

    class TestModel<T> {
        T someData;

        public TestModel() {

        }

        public TestModel(T someData) {
            this.someData = someData;
        }
    }

    class HttpStream extends ServletInputStream {

        private final InputStream source;

        public HttpStream(InputStream source) {
            super();
            this.source = source;
        }

        public final InputStream getSource() {
            return source;
        }

        public int read() throws IOException {
            return this.source.read();
        }

        public void close() throws IOException {
            super.close();
            this.source.close();
        }

        @Override
        public boolean isFinished() {
            try {
                return source.available() > 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            try {
                return source.available() <= 0;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
    }
}