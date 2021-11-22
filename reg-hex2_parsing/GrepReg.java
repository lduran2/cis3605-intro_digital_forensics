/* /reg-hex2_parsing/GrepReg.java
 * Grep like tool that prints key headers.
 *
 * By        : Leomar Duran <https://github.com/lduran2/>
 * When      : 2021-11-21t21:16
 * Where     : Temple University
 * For       : CIS 3605
 * Version   : 1.1.0
 * Canonical : https://github.com/lduran2/cis3605-intro_digital_forensics/blob/master/reg-hex2_parsing/RegGrep.java
 *
 * CHANGELOG :
 *     v1.1.0 - 2021-11-21t21:16
 *         processing and printing while reading
 *
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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/* alias STDIN and STDOUT */
import static java.lang.System.in;
import static java.lang.System.out;

public enum GrepReg {
	;

	/* set the color to bright red on terminal */
	public static String ANSI_BRIGHT_RED = "\u001B[31;1m";
	/* resets the color to the default */
	public static String ANSI_RESET = "\u001B[0m";

	public static void main(final String... args) {
		/* current line read */
		String line;
		/* current header if unused */
		String header = null;
		boolean must_print_empty_line = false;

		/* stop if no arguments bcs no pattern */
		if (args.length < 1) {
			out.printf("%s\n", "Usage: java GrepReg [PATTERN]");
			return;
		}
		/* compile the regular expression */
		final Pattern PATTERN = Pattern.compile(args[0]);

		/* buffered reader for STDIN */
		try (final BufferedReader BRIN = new BufferedReader(
					new InputStreamReader(in)))
		{
			while ((line = BRIN.readLine()) != null) {
				/* if an empty line */
				if (line.length() == 0) {
					if (must_print_empty_line) {
						out.printf("\n");
						must_print_empty_line
							= false;
					}
				} /* if (line.length() == 0) */
				/* if a header */
				else if (line.charAt(0) == '[') {
					header = line;
				} /* (line.length() == 0)
					else if (line.charAt(0) == '[')
				   */
				/* if any other type of line */
				else
				{
					header = matchLine(line, PATTERN, header, out);
				} /* (line.length() == 0) || (line.charAt(0) == '[')
					else if (!destRegExport.isEmpty()) {
				   */
			} /* while ((line = BRIN.readLine()) != null) */
		} /* try (new BufferedReader(...System.in)) */
		catch (final IOException IOE) {
			/* no recovery from IOException */
			throw new IOError(IOE);
		} /* catch (final IOException IOE) */
	} /* public static void main(final String... args) */

	/**
	 * Matches the pattern to the given line, printing the header
	 * if it's unused (not null), to the given print stream.
	 * @param line : String = the search
	 * @param pattern : Pattern = to match 
	 * @param header : String = of the key being matched
	 * @param dest : PrintStream = to which to print
	 * @return null if the header was used, `header` otherwise
	 */
	public static String matchLine(
			final String line, final Pattern pattern,
			final String header,
			final PrintStream dest)
		throws IOException
	{
		/* split the line by the pattern */
		final String[] SPLITS = pattern.split(line);
		final int N_SPLITS = SPLITS.length;
		/* filter if it does not contain pattern */
		/* i.e. string was split into at most 1 string */
		if (SPLITS.length <= 1) {
			/* header was not used */
			return header;
		} /* if (SPLITS.length <= 1) */

		/* print the header is not already used */
		if (null != header) {
			/* print the header */
			dest.printf("\n%s\n", header);
		} /* if (null != header) */

		/* find the matches between splits */
		final Matcher MATCHER = pattern.matcher(line);

		highlightMatches(N_SPLITS, SPLITS, MATCHER, dest);

		/* header has been used */
		return null;
	} /* public static void matchLine(
			final String line, final Pattern pattern,
			final String header,
			final PrintStream dest)
		throws IOException
	   */

	/**
	 * Combines the given splits and matcher to print a highlighted
	 * string.
	 * @param nSplits : int = number of splits
	 * @param splits : String[] = parts of the strings that did not
	 * 	match
	 * @param matcher : Matcher = returns the matching groups
	 * @param dest : PrintStream = to which to print
	 */
	public static void highlightMatches(
			final int nSplits, final String[] splits,
			final Matcher matcher,
			final PrintStream dest)
		throws IOException
	{
		/* print the line */
		/* while printing the splits */
		for (int k = 0;
			printingStrings(nSplits, splits, k, dest)
				/* fetch next match */
				&& matcher.find();
			++k)
		{
			/* highlight and print matches between */
			dest.printf("%s", ANSI_BRIGHT_RED);
			dest.printf("%s", matcher.group());
			dest.printf("%s", ANSI_RESET);
		} /* for (int k = 0;
			printingString(N_SPLIT, SPLIT, k, dest); ++k)
		   */
		/* and print the end line */
		dest.printf("\n");
	} /* public static void highlightMatches(
			final String line, final Pattern pattern,
			final int nSplits, final String[] splits,
			final PrintStream dest)
		throws IOException
	   */

	/**
	 * Prints the given string at the given index.
	 * @param len : int = number of strings
	 * @param strings : String[] = the strings to print
	 * @param idx : int = index of the current string
	 * @return true if there is a next string; false otherwise
	 */
	public static boolean printingStrings(
			final int len, final String[] strings,
			final int idx,
			final PrintStream dest)
		throws IOException
	{
		/* print the current string */
		dest.printf("%s", strings[idx]);
		/* return if next string */
		return ((idx + 1) != len);
	} /* public static void printingStrings(
			final int len, final String[] strings,
			final int idx,
			final PrintStream dest)
		throws IOException
	   */


} /* public enum GrepReg */

