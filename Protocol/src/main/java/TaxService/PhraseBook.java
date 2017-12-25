package TaxService;

public class PhraseBook
{
	public static final int PORT = 10650;
	public static final char SEPARATOR = '$';
	public static final String WRONG_SYMBOLS = "\"\'" + SEPARATOR;

	public static final String AUTH = "auth";
	public static final String BYE = "bye";
	public static final String ACCESS = "access";
	public static final String YES = "+";
	public static final String NO = "-";
	public static final String ERROR = "err";
	public static final String NOTIFICATION = "ntfc";

	private PhraseBook(){
	}
}
