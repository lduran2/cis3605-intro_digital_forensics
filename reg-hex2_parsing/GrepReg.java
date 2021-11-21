/* /reg-hex2_parsing/GrepReg.java
 * Grep like tool that prints key headers.
 *
 * By        : Leomar Duran <https://github.com/lduran2/>
 * When      : 2021-11-21t18:07
 * Where     : Temple University
 * For       : CIS 3605
 * Version   : 1.0.0
 * Canonical : https://github.com/lduran2/cis3605-intro_digital_forensics/blob/master/reg-hex2_parsing/RegGrep.java
 *
 * CHANGELOG :
 *     v1.0.0 - 2021-11-21t18:07
 *         store the reg export
 */

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.IOException;
import java.io.IOError;

import java.util.Deque;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/* alias STDIN and STDOUT */
import static java.lang.System.in;
import static java.lang.System.out;

public enum GrepReg {
	;

	public static void main(final String... args) {
		String line;

		/* list of the headings */
		final List<String> HEADINGS = new LinkedList<>();
		/* the reg export structure */
		/* maps strings to ordered line sets */
		final Map<String,List<String>> REG_EXPORT = new TreeMap<>();

		List<String> current_lines = null;

		/* buffered reader for STDIN */
		try (final BufferedReader BRIN = new BufferedReader(
					new InputStreamReader(in)))
		{
			while ((line = BRIN.readLine()) != null) {
				final int LEN = line.length();
				/* if an empty line */
				if (LEN == 0) {
				}
				/* if a heading */
				else if (line.charAt(0) == '[') {
					/* store heading */
					HEADINGS.add(line);
					current_lines = new LinkedList<>();
					REG_EXPORT.put(line, current_lines);
				}
				/* if in a line set */
				else if (!REG_EXPORT.isEmpty()) {
					current_lines.add(line);
				}
			}
			out.printf("%s", REG_EXPORT);
		} /* try (new BufferedReader(...System.in)) */
		catch (final IOException IOE) {
			/* no recovery from IOException */
			throw new IOError(IOE);
		} /* catch (final IOException IOE) */
	} /* public static void main(final String... args) */

} /* public enum GrepReg */

