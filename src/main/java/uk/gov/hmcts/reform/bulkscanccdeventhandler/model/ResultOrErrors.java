package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Result of retrieving/parsing an object.
 * <p>
 * Contains either the retrieved object or a list of error messages.
 * </p>
 */
public class ResultOrErrors<T> {

    public final Optional<T> result;
    public final List<String> errors;

    private ResultOrErrors(Optional<T> result, List<String> errors) {
        this.result = result;
        this.errors = errors;
    }

    public static <T> ResultOrErrors<T> result(T result) {
        return new ResultOrErrors<>(Optional.ofNullable(result), emptyList());
    }

    public static <T> ResultOrErrors<T> errors(List<String> errors) {
        return new ResultOrErrors<>(Optional.empty(), errors);
    }

    public static <T> ResultOrErrors<T> errors(String... errors) {
        return new ResultOrErrors<>(Optional.empty(), Arrays.asList(errors));
    }

    public boolean isSuccessful() {
        return errors.isEmpty();
    }

    public T get() {
        return result.orElse(null);
    }
}
