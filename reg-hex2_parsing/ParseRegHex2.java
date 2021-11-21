/* /reg-hex2_parsing/ParseRegHex2.java
 * Parses all hex(2) keys input in REG EXPORT format.
 *
 * By        : Leomar Duran <https://github.com/lduran2/>
 * When      : 2021-11-20t23:43
 * Where     : Temple University
 * For       : CIS 3605
 * Version   : 1.0.1
 * Canonical : https://github.com/lduran2/cis3605-intro_digital_forensics/blob/master/reg-hex2_parsing/ParseRegHex2.java
 *
 * CHANGELOG :
 *     v1.0.2 - 2021-11-21t03:57
 *         fixed printing as binary
 *
 *     v1.0.1 - 2021-11-20t23:43
 *         print all non-hex2 lines
 *
 *     v1.0.0 - 2021-11-20t23:30
 *         read all lines
 */

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.IOError;

/* alias STDIN and STDOUT */
import static java.lang.System.in;
import static java.lang.System.out;

public enum ParseRegHex2 {
	;

	/* marks the beginning of a hex2 */
	public static final String HEX2_START = "=hex(2):";

	/* length of the signature */
	public static final int SIGNATURE_LEN = 2;

	public static void main(final String... args) {
		String line;	/* current line read */

		/* buffered reader for STDIN */
		try (final BufferedReader BRIN = new BufferedReader(
					new InputStreamReader(in)))
		{
			/* read in the signature */
			final byte[] SIGNATURE = new byte[SIGNATURE_LEN];
			final int NB_READ = in.read(SIGNATURE);

			/* if not long enough */
			if (NB_READ < SIGNATURE_LEN) {
				/* stop now */
				return;
			} /* if (NB_LEN < SIGNATURE_LEN) */

			/* otherwise, write the signature */
			out.write(SIGNATURE);
			out.flush();

			/* read all lines from here on */
			while ((line = BRIN.readLine()) != null) {
				/**
				 * remove any null characters
				 * because programs will treat this as
				 * a binary file if there are null
				 * characters in the first 3000
				 * @see https://github.com/desktop/desktop/issues/6175
				 */
				line = removeNulls(line, line.length());
				/* if not a hex2 string */
				if (findHex2(line) == -1)
				{
					/* just print the line */
					out.printf("%s\n", line);
				} /* if (!isHex2(line)) */
			} /* while ((line = BRIN.readLine()) != null) */
		} /* try (new BufferedReader(...System.in)) */
		catch (final IOException IOE) {
			/* no recovery from IOException */
			throw new IOError(IOE);
		} /* catch (final IOException IOE) */
	} /* public static void main(final String... args) */

	/**
	 * Returns whether the string `s` is a hex2 assignment.
	 * @param s : String = to test
	 * @return true if `s` is a hex2 assignment; false otherwise
	 */
	public static int findHex2(final String s) {
		/* find opening quotation mark */
		final int I_QUOTE0 = s.indexOf("\"");
		/* find closing quotation mark */
		final int I_QUOTE1 = s.indexOf("\"", I_QUOTE0);
		/* stop if no quotation marks */
		if (I_QUOTE1 == -1) {
			return -1;
		} /* if (I_QUOTE1 == -1) */

		/* beginning of HEX2 assignment */
		final int I_BEGIN = (I_QUOTE1 + 1);
		/* end of HEX2 assignment */
		final int I_END = (I_BEGIN + HEX2_START.length());
		/* stop if string is too short */
		if (s.length() < I_END) {
			return -1;
		} /* if (s.length() < I_END) */

		/* get the substring corresponding to HEX2_START */
		final String h2subs = s.substring(I_BEGIN, I_END);
		/* if the substring is not equal */
		if (!h2subs.equals(HEX2_START)) {
			/* return not found */
			return -1;
		} /* if (!h2subs.equals(HEX2_START)) */
		else {
			/* otherwise, return the begin index */
			return I_BEGIN;
		}
	} /* public static int findHex2(final String s) */

	/**
	 * Removes all null characters in a given character sequence up
	 * to length `len`.
	 * @param cs : CharSequence = to clean up
	 * @param len : int = maximum number of characters to filter
	 * @return a string similar to the character sequence, but
	 * without null characters within the first `len` characters
	 */
	public static String removeNulls(final CharSequence cs, final int len) {
		/* buffer same size as string */
		final StringBuilder SB = new StringBuilder(len);
		/* loop through all the characters */
		for (int k = 0; k < len; ++k) {
			/* if not '\0' */
			if (cs.charAt(k) != 0) {
				/* append the character */
				SB.append(cs.charAt(k));
			} /* if (cs.charAt(k) != 0) */
		}
		/* build the string and return it */
		return SB.toString();
	} /* public static String removeNulls(final CharSequence cs, final int len) */

} /* public enum ParseRegHex2 */

