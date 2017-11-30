package Interfaces;

/**
 * Created by Marius Baltramaitis on 07/06/2017.
 * <p>Simple interface with three type parameters, might as well be used as functional interface</p>
 * <p> R - return type</p>
 * <p> P1 - first parameter</p>
 * <p> P21 - second parameter</p>
 */
@FunctionalInterface
public interface MultipleParameterFunction<R,P1,P2> {

    /**
     * Apply method
     * @param firstParameter first parameter(P1)
     * @param secondParameter second parameter(P2)
     * @return return value defined in type parameters(R)
     */
    R apply (P1 firstParameter,P2 secondParameter);
}
