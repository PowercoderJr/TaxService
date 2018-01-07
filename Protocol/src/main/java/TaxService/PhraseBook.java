package TaxService;

public class PhraseBook
{
	public static final int PORT = 10650;
	public static final int PING_FREQUENCY_MILLIS = 10000;
	public static final int CONNECTION_TIMEOUT_MILLIS = PING_FREQUENCY_MILLIS * 3;
	public static final char SEPARATOR = '$';

	public static final String AUTH = "auth";
	public static final String BYE = "bye";
	public static final String ACCESS = "access";
	public static final String ACCESS_RESULT_SUCCESS = "succ";
	public static final String ACCESS_RESULT_INVALID_LOGIN_PASSWORD = "invpl";
	public static final String ACCESS_RESULT_ALREADY_LOGGED = "alrlogd";
	public static final String ERROR = "err";
	public static final String NOTIFICATION = "ntfc";
	public static final String PING = "P";
	public static final String QUERY = "query";

	private PhraseBook(){
	}
}
