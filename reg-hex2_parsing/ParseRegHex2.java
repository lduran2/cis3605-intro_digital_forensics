/* /reg-hex2_parsing/ParseRegHex2.java
 * Parses all hex(2) keys input in REG EXPORT format.
 *
 * By        : Leomar Duran <https://github.com/lduran2/>
 * When      : 2021-11-21t16:41
 * Where     : Temple University
 * For       : CIS 3605
 * Version   : 1.1.2
 * Canonical : https://github.com/lduran2/cis3605-intro_digital_forensics/blob/master/reg-hex2_parsing/ParseRegHex2.java
 *
 * CHANGELOG :
 *     v1.1.2 - 2021-11-21t16:41
 *         abstracted out `processPairs` and `processCharPair`
 *
 *     v1.1.1 - 2021-11-21t15:56
 *         decoding hex2
 *
 *     v1.1.0 - 2021-11-21t12:50
 *         identifying when in hex2
 *
 *     v1.0.4 - 2021-11-21t12:19
 *         printing all immediate hex2 lines
 *
 *     v1.0.3 - 2021-11-21t05:07
 *         abstracted out `copySignature` and `processLines`
 *
 *     v1.0.2 - 2021-11-21t03:57
 *         fixed printing as binary
 *
 *     v1.0.1 - 2021-11-20t23:43
 *         print all non-hex2 lines
 *
 *     v1.0.0 - 2021-11-20t23:30
 *         read all lines
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.IOError;

/* alias STDIN and STDOUT */
import static java.lang.System.in;
import static java.lang.System.out;

public enum ParseRegHex2 {
	;

	/* marks the beginning of a hex2 */
	public static final String HEX2_START = "=hex(2):";
	public static final int HEX2_START_LEN = HEX2_START.length();

	/* length of the signature */
	public static final int SIGNATURE_LEN = 2;

	public static void main(final String... args) {
		/* buffered reader for STDIN */
		try (final BufferedReader BRIN = new BufferedReader(
					new InputStreamReader(in)))
		{
			copySignature(in, out);
			processLines(BRIN, out);
		} /* try (new BufferedReader(...System.in)) */
		catch (final IOException IOE) {
			/* no recovery from IOException */
			throw new IOError(IOE);
		} /* catch (final IOException IOE) */
	} /* public static void main(final String... args) */

	/**
	 * Copies the signature of `src` into `dest`.
	 * @param src : InputStream = the source bytestream
	 * @param dest : OutputStream = the destination bytestream
	 * @throws IOException if an unexpected IO error occurs
	 */
	public static void copySignature(
			final InputStream src, final OutputStream dest)
		throws IOException
	{
		/* read in the signature */
		final byte[] SIGNATURE = new byte[SIGNATURE_LEN];
		final int NB_READ = src.read(SIGNATURE);

		/* if not long enough */
		if (NB_READ < SIGNATURE_LEN) {
			/* stop now */
			return;
		} /* if (NB_LEN < SIGNATURE_LEN) */

		/* otherwise, write the signature */
		dest.write(SIGNATURE);
		/* ensure write through to file */
		dest.flush();
	} /* public static void copySignature(
			final InputStream src, final OutputStream dest)
		throws IOException
	   */

	/**
	 * Reads lines from buffered reader `src`, processes each line,
	 * and prints it to print stream `dest`.
	 * @param src : BufferedReader = the source of the lines of
	 *     data
	 * @param dest : PrintStream = the destination of the processed
	 * 	data
	 * @throws IOException if an unexpected IO error occurs
	 */
	public static void processLines(
			final BufferedReader src, final PrintStream dest)
		throws IOException
	{
		String line;	/* current line read */
		int i_hex2;	/* index of hex2 assignment in `line` */
		boolean in_hex2 = false;	/* whether parser is in
		                        	 * a hex2 string */
		/* builds the pairs string */
		StringBuilder pairs_sb = null;

		/* read all lines from here on */
		while ((line = src.readLine()) != null) {
			/**
			 * remove any null characters
			 * because programs will treat this as
			 * a binary file if there are null
			 * characters in the first 3000
			 * @see https://github.com/desktop/desktop/issues/6175
			 */
			line = removeNulls(line.length(), line);
			/* if in a hex2 string */
			if (in_hex2) {
				/* wait */
			} /* if (in_hex2) */
			/* if not new a hex2 string */
			else if ((i_hex2 = findHex2(line)) == -1)
			{
				/* just print the line */
				dest.printf("%s\n", line);
			} /* if ((i_hex2 = findHex2(line)) == -1) */
			else {
				/* print the line up to i_hex32 */
				dest.printf("%s", line.substring(0, i_hex2));
				dest.printf("%s", HEX2_START.substring(1));
				/* update line */
				line = line.substring(i_hex2 + HEX2_START.length() - 1);
				/* state now in_hex2 */
				in_hex2 = true;
				/* create string builder */
				pairs_sb = new StringBuilder();
			} /* (in_hex2) || ((i_hex2 = findHex2(line)) == -1)
				else */

			/* if in a hex2 string */
			if (in_hex2) {
				/* check if next line still in hex2 */
				in_hex2 = isInHex2(line);
				/* if still in hex2 */
				if (in_hex2) {
					/* don't append the '\' */
					final String LINE_PAIRS
						= line.substring(0,
							(line.length() - 1)
						);
					pairs_sb.append(LINE_PAIRS);
				} /* if (in_hex2) */
				else {
					/* append the last line */
					pairs_sb.append(line);
					/* print the processed pairs */
					dest.printf("%s\n", processPairs(
						pairs_sb.toString()));
				} /* if (in_hex2) */
			} /* if (in_hex2) */

			/* clear '\n' in '\r\n' */
			src.readLine();
		} /* while ((line = src.readLine()) != null) */
	} /* public static void processLines(
			final BufferedReader src, final PrintStream dest)
		throws IOException
	   */

	/**
	 * Returns whether the string `s` is a hex2 assignment.
	 * @param s : String = to test
	 * @return true if `s` is a hex2 assignment; false otherwise
	 */
	public static int findHex2(final String s) {
		/* find opening quotation mark */
		final int I_QUOTE0 = s.indexOf("\"");
		/* find closing quotation mark */
		final int I_QUOTE1 = s.indexOf("\"", (I_QUOTE0 + 1));
		/* filter out line without quotes names/values */
		if (I_QUOTE1 == -1) {
			return -1;
		} /* if (I_QUOTE1 == -1) */

		/* beginning of HEX2 assignment */
		final int I_BEGIN = (I_QUOTE1 + 1);
		/* end of HEX2 assignment */
		final int I_END = (I_BEGIN + HEX2_START_LEN);
		/* filter out lines that are too short */
		if (s.length() < I_END) {
			return -1;
		} /* if (s.length() < I_END) */

		/* get the substring corresponding to HEX2_START */
		final String h2subs = s.substring(I_BEGIN, I_END);
		/* filter out strings not containing HEX2_START there */
		if (!h2subs.equals(HEX2_START)) {
			/* return not found */
			return -1;
		} /* if (!h2subs.equals(HEX2_START)) */
		else {
			/* otherwise, return the begin index */
			return (I_BEGIN + 1);
		} /* (!h2subs.equals(HEX2_START)) else */
	} /* public static int findHex2(final String s) */

	/**
	 * Returns whether the line is still in hex2.
	 * @param line : String = to test
	 * @return true if still in hex2; false otherwise
	 */
	public static boolean isInHex2(final CharSequence line) {
		/* filter nulls null */
		if (line == null) {
			return false;
		} /* if (line == null) */
		/* get the length */
		final int LEN = line.length();
		/* if last character is '\' */
		return ((LEN != 0) && (line.charAt(LEN - 1) == '\\'));
	} /* public static boolean isInHex2(final CharSequence line) */

	/**
	 * Processes a string of comma separated hex-formatted bytes
	 * representing a character string.
	 * @param s : String = the csv string to process
	 * @return the string represented as bytes
	 */
	public static String processPairs(final String s) {
		/* builds the output string */
		final StringBuilder SB = new StringBuilder();
		/* the csv string separated into values */
		final String[] S_PAIRS = s.split(",");
		for (int k = 0, len = S_PAIRS.length; (k < len); k +=2) {
			/* process the csv string */
			final char CHAR = processCharPair(S_PAIRS, k);
			/* if non-null character */
			if (0 != CHAR) {
				/* append to the string builder */
				SB.append(CHAR);
			} /* if (0 != CHAR) */
		} /* for (int k = 0, len = S_PAIRS.length; (k < len); k +=2)
		   */
		/* terminate and return string */
		return SB.append("\"").toString();
	} /* public static String processPairs(final String s) */

	/**
	 * Processes a single pair of hex-formatted integers
	 * representing a character at index `idx`.
	 * @param sPairs : String[] = array containing pair
	 * @param idx : int = the index at which to process
	 * @return the character processed
	 */
	public static char processCharPair(
			final String[] sPairs, final int idx)
	{
		/* big endian, so HI follows */
		/* also may have leading space */
		final String SHI = sPairs[idx | 1].trim().toUpperCase();
		final String SLO = sPairs[idx].trim().toUpperCase();
		/* convert to integers */
		final int IHI = Integer.valueOf(SHI, 16);
		final int ILO = Integer.valueOf(SLO, 16);
		/* convert to a single byte and return */
		return ((char)((IHI << (2*4)) | ILO));
	} /* public static char processCharPair(
			final String[] sPairs, final int idx)
	   */

	/**
	 * Removes all null characters in a given character sequence up
	 * to length `len`.
	 * @param len : int = maximum number of characters to filter
	 * @param cs : CharSequence = to clean up
	 * @return a string similar to the character sequence, but
	 * without null characters within the first `len` characters
	 */
	public static String removeNulls(
			final int len, final CharSequence cs)
	{
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
	} /* public static String removeNulls(
			final int len, final CharSequence cs))
	   */

} /* public enum ParseRegHex2 */

