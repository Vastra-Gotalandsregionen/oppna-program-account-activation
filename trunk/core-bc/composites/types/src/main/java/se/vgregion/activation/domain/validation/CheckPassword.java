package se.vgregion.activation.domain.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * JSR-303 annotation for PasswordFormBean validation.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckPasswordValidator.class)
@Documented
public @interface CheckPassword {
    /**
     * Declaring key for default error message lookup.
     *
     * @return lookup key.
     */
    String message() default "{vgr.accoutactivation.password}";

    /**
     * No group behaivour.
     *
     * @return empty.
     */
    Class<?>[] groups() default { };

    /**
     * No payload.
     *
     * @return empty.
     */
    Class<? extends Payload>[] payload() default { };
}
