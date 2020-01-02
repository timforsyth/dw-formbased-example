package uk.co.chunkybacon.dwformbased.resource.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.Response;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse<E> {

    private int code;
    private String message;
    private E data;

    @JsonProperty
    public int code() {
        return code;
    }

    @JsonProperty
    public String message() {
        return message;
    }

    @JsonProperty
    public E data() {
        return data;
    }

    public Response asResponse() {
        return Response.status(code).entity(this).build();
    }

    public static class DataCollection<T> {

        private Integer total;
        private List<T> items;

        @JsonProperty
        public Integer total() {
            return total;
        }

        @JsonProperty
        public List<T> items() {
            return items;
        }
    }

    public static class SingleItemBuilder<T> {

        private ApiResponse<T> response;

        public SingleItemBuilder() {
            this.response = new ApiResponse<>();
        }

        public ApiResponse<T> build() {
            return this.response;
        }

        public SingleItemBuilder<T> status(int status) {
            this.response.code = status;
            return this;
        }

        public SingleItemBuilder<T> message(String message) {
            this.response.message = message;
            return this;
        }

        public SingleItemBuilder<T> data(T data) {
            this.response.data = data;
            return this;
        }
    }

    public static class CollectionBuilder<T> {

        private ApiResponse<DataCollection<T>> response;

        public CollectionBuilder() {
            this.response = new ApiResponse<>();
            this.response.data = new DataCollection<>();
        }

        public ApiResponse<DataCollection<T>> build() {
            return this.response;
        }

        public CollectionBuilder<T> status(int status) {
            this.response.code = status;
            return this;
        }

        public CollectionBuilder<T> message(String message) {
            this.response.message = message;
            return this;
        }

        public CollectionBuilder<T> items(List<T> items) {
            this.response.data.items = items;
            this.response.data.total = items.size();
            return this;
        }
    }

}
