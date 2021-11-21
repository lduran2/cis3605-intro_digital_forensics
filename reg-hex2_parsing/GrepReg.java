/* /reg-hex2_parsing/GrepReg.java
 * Grep like tool that prints key headers.
 *
 * By        : Leomar Duran <https://github.com/lduran2/>
 * When      : 2021-11-21t18:41
 * Where     : Temple University
 * For       : CIS 3605
 * Version   : 1.0.1
 * Canonical : https://github.com/lduran2/cis3605-intro_digital_forensics/blob/master/reg-hex2_parsing/RegGrep.java
 *
 * CHANGELOG :
 *     v1.0.1 - 2021-11-21t18:41
 *         abstracted out `storeRegExport`
 *
 *     v1.0.0 - 2021-11-21t18:07
 *         store the reg export
 */

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.IOException;
import java.io.IOError;

import java.util.Collection;
//import java.util.Deque;
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
		/* list of the headings */
		final List<String> HEADINGS = new LinkedList<>();
		/* the reg export structure */
		/* maps strings to ordered line sets */
		final Map<String,List<String>> REG_EXPORT = new TreeMap<>();
		/* stores the current lines being added */
		final List<String> CURRENT_LINES = new LinkedList<>();

		/* buffered reader for STDIN */
		try (final BufferedReader BRIN = new BufferedReader(
					new InputStreamReader(in)))
		{
			storeRegExport(BRIN, HEADINGS, REG_EXPORT, CURRENT_LINES);
			out.printf("%s", REG_EXPORT);
		} /* try (new BufferedReader(...System.in)) */
		catch (final IOException IOE) {
			/* no recovery from IOException */
			throw new IOError(IOE);
		} /* catch (final IOException IOE) */
	} /* public static void main(final String... args) */

	public static <TStrings extends Collection<String>>
		void storeRegExport(
			final BufferedReader src,
			final TStrings destHeadings,
			final Map<String,TStrings> destRegExport,
			final TStrings tmpCurrentLines)
		throws IOException
	{
		/* current line read */
		String line;

		while ((line = src.readLine()) != null) {
			/* if an empty line */
			if (line.length() == 0) {
				/* pass */
			} /* if (line.length() == 0) */
			/* if a heading */
			else if (line.charAt(0) == '[') {
				/* store heading */
				destHeadings.add(line);
				/* empty the lines */
				tmpCurrentLines.clear();
				/* store the line set */
				destRegExport.put(line, tmpCurrentLines);
			} /* (line.length() == 0)
				else if (line.charAt(0) == '[')
			   */
			/* if in a line set */
			else if (!destRegExport.isEmpty()) {
				/* add this line to the set */
				tmpCurrentLines.add(line);
			} /* (line.length() == 0) || (line.charAt(0) == '[')
				else if (!destRegExport.isEmpty()) {
			   */
		} /* while ((line = src.readLine()) != null) */
	} /* public static <TStrings extends Collection<String>>
		void storeRegExport(
			final BufferedReader src,
			final TStrings destHeadings,
			Map<String,TStrings> destRegExport)
	   */

} /* public enum GrepReg */

