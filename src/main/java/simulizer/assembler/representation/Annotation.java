package simulizer.assembler.representation;

/**
 * A piece of JavaScript code which is bound to a statement and executes when
 * the statement is executed. The code is used to coordinate high level
 * algorithm visualisations.
 *
 * @author mbway
 */
public class Annotation {

    public final String code;

	/**
	 * Create a new annotation with the given code.
	 * @param code the code to be run
	 */
	public Annotation(String code) {
		this.code = code;
	}

    @Override
    public String toString() {
        return code;
    }
}
