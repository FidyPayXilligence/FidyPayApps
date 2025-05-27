package com.fidypay.utils.constants;

public class ResponseMessage {

	// Response Code
	public static final String SUCCESS = "0x0200";
	public static final String UNAUTHORISED = "0x0201";
	public static final String FAILED = "0x0202";
	public static final String MISSING_PARAMETER = "0x0203";
	public static final String CONNECTION_TIMEOUT = "0x0204";
	public static final String SOMETHING_WENT_WRONG = "0x0205";
	public static final String BAD_REQUEST = "0x0202";

	// Response Key
	public static final String CODE = "code";
	public static final String DESCRIPTION = "description";
	public static final String MERCHANT_TXN_REF_ID = "merchantTxnRefId";
	public static final String FIELD = "field";
	public static final String STATUS = "status";
	public static final String DETAILS = "details";

	// Response Field
	public static final String FIELD_I = "Client-Id, Client-Secret";

//  Live
	public static final String CKYC_TOKEN = "k9dwre5WxXktUrdLblewgH1YanRFlgDY";
	public static final String CKYC_SEARCH_URL = " https://api.signzy.app/api/v3/ckycSearch";
	public static final String CKYC_DOWNLOAD_URL = "https://api.signzy.app/api/v3/ckycDownload";

	// Response Description
	public static final String UNAUTHORISED_DESCRIPTION = "Unauthorized Access to FidyPay Platform";
	public static final String DEBIT_AMOUNT_NOT_AVAILABLE = "Debit Amount not available";
	public static final String MERCHANTTRXNREFID_ALREADY_EXIST = "merchantTrxnRefId already exist please try unique id";
	public static final String MISSING_PARAMETER_DESCRIPTION = "Some Parameter are missing ";
	public static final String CONNECTION_TIMEOUT_DESCRIPTION = "Connection Timeout";
	public static final String SOMETHING_WENT_WRONG_DESCRIPTION = "Something Went Wrong";
	public static final String SERVICE_NOT_AVILABLE = "Service Not Avilable";
	public static final String ONE_LAKH_LIMIT = "UPI ID/VPA OR Merchant TrxnRefId are missing OR Amount limit Exceed 1 lac limit";
	public static final String TWO_LAKH_LIMIT = "UPI ID/VPA OR Merchant TrxnRefId are missing OR Amount limit Exceed 2 lac limit";
	public static final String VPA_MISSING = "VPA are missing ";
	public static final String TRANSACTION_REFUND = "Transaction Already Refunded";
	public static final String INVALID_IFSCCODE = "beneficiaryifsccode -> Invalid beneficiaryifsccode";
	public static final String SELECT_FILE = "Please select file to upload";
	public static final String SERVICEID_NOT_EXIST = "To subscribe for this service, get in touch with the FidyPay team.";
	public static final String SERVICE_CLOSED = "This service has been discontinued. Please use the updated service for this API or reach out to helpdesk@fidypay.com for further assistance.";

	public final static String TRANSACTION_ID_NOT_VALID = "merchantTxnRefId  is not valid";
	public final static String DOCUMENT_ID_NOT_VALID = "documentId is not valid";
	public static final Object TRXNID_ALREADY_EXSIST = "merchantTrxnRefId already exist";

	public final static String WORKFLOW_UNIQUE_ID = "workflowUniqueId";
	public final static String USER_UNIQUE_ID = "userUniqueId";
	public final static String WORKFLOW_UNIQUE_ID_NOT_VALID = "workflowUniqueId is does not valid";
	public final static String WORKFLOW_UNIQUE_ID_NOT_EXIST = "workflowToken is does not exist";
	public final static String USER_UNIQUE_ID_NOT_EXIST = "userWorkflowToken is not exist";

	public final static String EKYC_EMAIL = "mobile and email already exist";
	public final static String PAN_CARD_INVALID = "pan card number is invalid";
	public final static String VOTER_ID_INVALID = "epic number is invalid";
	public final static String GST_NUMBER_INVALID = "GST number is invalid";
	public final static String AADHAR_NUMBER_INVALID = "aadhar number should be 12 digit";
	public final static String DATE_FORMATE_INVALID = "date formate invalid -> Please enter a date in format  dd/MM/yyyy";
	public final static String INVALID_JSON_FORMATE = "Invalid Request";
	public final static String INVALID_VPA = "Invalid VPA";
	public static final String VALID_PAN_NUMBER = "Please enter valid pan number";
	public final static String SUCCESSFULLY_REGISTERED = "Request generated successfully!";
	public final static String WORKFLOW_NAME_ALREADY_REGISTERED = "Workflow name already registered.";
	public final static String USER_ALREADY_REGISTERED = "KYC request already sent to user";
	public final static String STATUS_TIMEOUT = "TIMEOUT";
	public final static String STATUS_REFUND = "Refunded";
	public final static String STATUS_SUCCESS = "Success";
	public final static String STATUS_PENDING = "Pending";
	public final static String STATUS_FAILED = "Failed";
	public final static String STATUS_REVERSED = "Reversed";

	public final static String DATA_NOT_FOUND = "Data not found.";
	public final static String USER_NOT_FOUND = "No records found for the given ID.";
	public final static String DATA = "data";
	public final static String DATA_FOUND = "Data found successfully";

	public static final String API_STATUS_SUCCESS = "SUCCESS";
	public static final String API_STATUS_PENDING = "PENDING";
	public static final String API_STATUS_FAILED = "FAILED";
	public static final String API_STATUS_REFUND = "REFUND";
	public static final String INVALID_DATE_FORMATE = "Invalid Date Format please try dd-MM-yyyy format";
	public static final String INVALID_FILE_FORMAT = "Invalid file format. Please pass pdf,jpg,jpeg,png format";
	public static final String INVALID_IMAGE_FORMAT = "Invalid file format. Please pass pdf,tiff,jpeg,png format";
	public static final String INVALID_VIDEO_FORMAT = "Invalid file format. Please pass mp4/webm";
	public static final String IMAGE_FORMAT = "Invalid file format. Please pass jpg,png format";
	public static final String FILE_SIZE = "Maximum file size 50 MB";
	public static final String VIDEO_SIZE = "Maximum file size 20 MB";
	public static final String IMAGE_SIZE = "Maximum file size 7 MB";
	public static final String IMAGE_URL = "Please pass image URL";
	public static final String DATA_SUCCESS = "Data fetch successfully.";
	public static final String DOCUMENT_UPLOADED = "Document upload successfully";
	public static final String HEADERS_MISSING = "Missing Required Headers";
	public static final String INVALID_DOCUMENT = "Please pass AADHAAR,PAN,GST,DRIVING LICENSE,VOTER ID,PASSPORT,BANK ACCOUNT,FACE MATCH,IMAGE LIVENESS and REGISTRATION CERTIFICATE on fileName parameter";
	public static final String DUPLICATE_FILENAME = "Document already exist for this fileName.";

	public static final String DELETE = "Data has been deleted.";
	public static final String UPDATED = "Data has been updated.";

	public static final String OTP_BANKINFO_FAILED = "OTP Verification failed";
	public static final String OTP_BANKINFO_EXPIRED = "Your otp has expired, Please generate again";
	public static final String VALID_OTP_MERTRXNREFID = "OTP verified Successfully";

	public static final String MOBILE_NUMBER_NOT_EXIST = "Mobile number not exist.";
	public static final String WORKFLOW_DELETED_MESSAGE = "Workflow does not exist, please contact the administration.";
	public static final String CONTACT_TECH_SUPPORT = "Please try again after sometime. If the problem persists, please drop a mail to tech.support@fidypay.com";

	public static final String ELECTRICITY_BILL_DETAILS = "Electricity bill details";
	public static final String NAME_NOT_MATCH = "Name does not matched";
	public static final String NAME_MATCH = "Name match sucessfully";
	public static final String ADDRESS_MATCH = "Address match sucessfully";
	public static final String ADDRESS_NOT_MATCH = "Address does not matched";
	public static final String TRY_AFTER_SOMETIME = "Something Went Wrong, Please Try After SomeTime";
	public static final String RANDOM_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz";
	public static final String ENC_DEC_KEY = "Bar12345Bar12345";
	public static final String SSO_KEY = "fidypay@general_insurance_tech_#";
	public static final String SSO_INITVECTOR = "fidypay@insure_#";
	public static final String LOGO_URL = "https://fidylogoandqrimages.s3.ap-south-1.amazonaws.com/";

	// UAT Providers and URL
	public static final String SIGNZY = "SIGNZY";
	public static final String DECENTRO = "DECENTRO";
	public static final String BEFISC = "BEFISC";
	
	public static final String KARZA = "KARZA TECH";
	public static final String ONE_BHARAT = "ONE BHARAT";
	public final static String MERCHANT_ENVIRONMENT = "UAT";
	public final static String LINK_WORKFLOW = "https://prekyc.fidypay.com/";
	public final static String ELECTRICITY_SERVICE = "Electricity Bill Authentication";
	public final static String CREDIT_SCORE_SERVICE = "Credit Score Detailed";
	public final static String CREDIT_SCORE_BASIC_SERVICE = "Credit Score Basic";
	public final static String TAN_SERVICE = "TAN Verification";
	public final static String VEHICLE_REG_SERVICE = "Vehicle Registration";
	public final static String EPF_UAN_SERVICE = "EPF UAN Verification";
	public final static String MOBILE_NO_VERIFICATION_SERVICE = "Mobile Number Verification";
	public final static String EMAIL_VERIFICATION_SERVICE = "Email Verification";

	// Production Providers and URL
//	public static final String SIGNZY = "Signzy";
//	public static final String DECENTRO = "Decentro";
//	public static final String KARZA = "Karza Tech";
//	public static final String ONE_BHARAT = "ONE BHARAT";
//	public final static String MERCHANT_ENVIRONMENT = "Production";
//	public final static String LINK_WORKFLOW = "https://userkyc.fidypay.com/";
//	public final static String ELECTRICITY_SERVICE = "ELECTRICITY BILL AUTHENTICATION";
//	public final static String CREDIT_SCORE_SERVICE = "CREDIT SCORE DETAILED";
//	public final static String CREDIT_SCORE_BASIC_SERVICE = "CREDIT SCORE BASIC";
//	public final static String TAN_SERVICE = "TAN VERIFICATION";
//	public final static String VEHICLE_REG_SERVICE = "VEHICLE REGISTRATION";
//	public final static String EPF_UAN_SERVICE = "EPF UAN VERIFICATION";
//	public final static String MOBILE_NO_VERIFICATION_SERVICE = "MOBILE NUMBER VERIFICATION";
//	public final static String EMAIL_VERIFICATION_SERVICE = "EMAIL VERIFICATION";

	// PROD
//	public final static String IP = "3.109.234.226:9200";
//	public static final String SERVER_HOST = "3.109.234.226";

	// UAT
//	public static final String SERVER_HOST = "13.127.244.132";
//	public final static String IP = "13.127.244.132:9200";

	// ELK
	// LOCAL
//	public static final String SERVER_HOST = "localhost";
//	public final static String IP = "localhost:9200";
//	//Prelive
//	public final static String LINK_WORKFLOW = "https://pwa.d2uocm7xuurb9y.amplifyapp.com/";

}