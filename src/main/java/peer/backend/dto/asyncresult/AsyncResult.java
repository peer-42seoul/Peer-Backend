package peer.backend.dto.asyncresult;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class AsyncResult<T> {
    private T result;
    private Exception exception;


    public static <T> AsyncResult<T> success(T result) {
        AsyncResult<T> asyncResult = new AsyncResult<>();
        asyncResult.result = result;
        return asyncResult;
    }

    public static <T> AsyncResult<T> failure(Exception exception) {
        AsyncResult<T> asyncResult = new AsyncResult<>();
        asyncResult.exception = exception;
        return asyncResult;
    }

    public boolean isSuccess() {
        return exception == null;
    }

}
