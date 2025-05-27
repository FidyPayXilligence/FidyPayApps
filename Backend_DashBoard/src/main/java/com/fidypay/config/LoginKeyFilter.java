package com.fidypay.config;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fidypay.encryption.Encryption;
import com.fidypay.entity.MerchantUser;
import com.fidypay.repo.MerchantUserRepository;
import com.fidypay.utils.constants.ResponseMessage;

@Component
public class LoginKeyFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginKeyFilter.class);

	@Autowired
	private MerchantUserRepository userRepository;

	public void setUserRepository(MerchantUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String loginKey = request.getHeader("Login-Key");

		if (loginKey == null || loginKey.equals("")) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json");
			response.getWriter().write(ResponseMessage.response());
			return;
		}

		if (loginKey != null) {
			Optional<MerchantUser> userOptional = userRepository.findByMerchantUserKey(loginKey);

			if (!userOptional.isPresent()) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.setContentType("application/json");
				response.getWriter().write(ResponseMessage.response());
				return;
			}

			if (userOptional.isPresent()) {
				MerchantUser user = userOptional.get();

				// Create an authentication token
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						Encryption.encString(user.getMerchantUserEmail()), null, null);

				// Set the authentication token in the SecurityContext
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		LOGGER.info("path : {}", path);
		return path.equals("/dashboard/accountDetails/checkMerchant")
				|| path.equals("/dashboard/accountDetails/getMerchantServiceDetails")
				|| path.equals("/dashboard/accountDetails/forgetMerchantPassowrd")
				|| path.equals("/dashboard/accountDetails/login")
				|| path.equals("/dashboard/accountDetails/login-merchant")
				|| path.equals("/dashboard/accountDetails/login-merchant-otp")
				|| path.equals("/dashboard/accountDetails/login-merchant-sandbox")
				|| path.startsWith("/dashboard/accountDetails/mobileNoLogin")
				|| path.startsWith("/dashboard/accountDetails/login-pwa")
				|| path.startsWith("/dashboard/accountDetails/verify-login-otp")
				|| path.equals("/dashboard/accountDetails/verifyMerchantEmail")
				|| path.equals("/dashboard/global/citiesList") || path.equals("/dashboard/global/citiesListByStateName")
				|| path.equals("/dashboard/global/country_list") || path.equals("/dashboard/global/statesList")
				|| path.equals("/dashboard/v2/updateTrxnIdentifierOnBBPSTrxnDeyails")
				|| path.equals("/dashboard/api/kyc/addUserKycDetails")
				|| path.equals("/dashboard/api/kyc/checkUserKycStatus")
				|| path.equals("/dashboard/api/kyc/findUserKycDetailsByUserMobile")
				|| path.equals("/dashboard/api/kyc/findUserKycDetailsByUserUniqueId")
				|| path.equals("/dashboard/api/kyc/updateUserKycDetails") || path.equals("/dashboard/logOut")
				|| path.equals("/dashboard/AddFloatByAdmin/POST")
				|| path.startsWith("/dashboard/addMerchantInfoByMerchantId")
				|| path.equals("/dashboard/saveMerchantInfo") || path.equals("/dashboard/CreateMerchantByAdmin/POST")
				|| path.equals("/dashboard/merchantService/forgetPassword")
				|| path.equals("/dashboard/merchantService/getotp")
				|| path.equals("/dashboard/merchantService/merchantDeactive")
				|| path.equals("/dashboard/merchantService/merchantLogin")
				|| path.equals("/dashboard/merchantService/merchantRegister")
				|| path.equals("/dashboard/merchantService/v2/merchantRegister")
				|| path.equals("/dashboard/serviceReport/getStatus")
				|| path.equals("/dashboard/merchantUser/addRecordByEmail")
				|| path.equals("/dashboard/merchantUser/addRecords") || path.equals("/dashboard/pwa/getSettlementList")
				|| path.equals("/dashboard/pwa/login") || path.equals("/dashboard/checkEmailId")
				|| path.equals("/dashboard/checkMobileNo") || path.startsWith("/dashboard/checkPanNumber")
				|| path.equals("/dashboard/getBussinessTypeList") || path.equals("/dashboard/getMCCDetailsList")
				|| path.equals("/dashboard/loginWithOTP") || path.equals("/dashboard/V2/merchantRegister")
				|| path.startsWith("/dashboard/subMerchantInfo/cbank/sub-merchant")
				|| path.startsWith("/dashboard/subMerchantInfo/checkLimit")
				|| path.startsWith("/dashboard/subMerchantInfo/findByMobile")
				|| path.startsWith("/dashboard/subMerchantInfo/findByMobileNumber")
				|| path.startsWith("/dashboard/subMerchantInfo/updateBySubMerchantId")
				|| path.equals("/dashboard/subMerchantInfo/getCitiesListByStateCode")
				|| path.equals("/dashboard/subMerchantInfo/getSubMerchantDetailsSheet")
				|| path.equals("/dashboard/subMerchantInfo/subMerchantListExcelName")
				|| path.startsWith("/dashboard/sub-merchant-temp/") && path.contains("/verifySubMerchant/")
				|| path.startsWith("/dashboard/sub-merchant-temp/") && path.contains("/otpVerification/")
				|| path.startsWith("/dashboard/accountDetails/") && path.contains("/mobileNoVerification/")
				|| path.startsWith("/dashboard/accountDetails/") && path.contains("/otpVerification/")
				|| path.startsWith("/dashboard/sub-merchant-temp/findByToken")
				|| path.startsWith("/dashboard/sub-merchant-temp/findByTokenNew")
				|| path.startsWith("/dashboard/sub-merchant-temp/resend-notification")
				|| path.equals("/dashboard/sub-merchant-temp/save-merchant-submerchant-Info")
				|| path.startsWith("/dashboard/sub-merchant-temp/save-merchant-submerchant-Info-date")
						&& path.contains("/to/")
				|| path.startsWith("/dashboard/sub-merchant-temp/save-merchant-submerchant-tempid")
				|| path.startsWith("/dashboard/sub-merchant-temp/sendOTPPhone")
				|| path.startsWith("/dashboard/sub-merchant-temp/update-details")
				|| path.equals("/dashboard/subMerchantInfo/getSubMerchantDetails")
				|| path.equals("/dashboard/subMerchantInfo/getCitiesListByStateName")
				|| path.equals("/dashboard/subMerchantInfo/findByMobile")
				|| path.equals("/dashboard/subMerchantInfo/findByMobileNumber")
				|| path.equals("/dashboard/sub-merchant-temp/update-details") || path.equals("/dashboard/upload-banner")
				|| path.equals("/dashboard/merchantUser/register")
				|| path.equals("/dashboard/v2/getBBPSTransactionsStatementReportExcel")
				|| path.equals("/dashboard/v2/bbpsCommissionsListReportExcel")
				|| path.equals("/dashboard/v2/getPayoutTransactionsStatementReportExcel")
				|| path.equals("/dashboard/getENachSettlementReportExcel")
				|| path.equals("/dashboard/v2/getENachTransactionsStatementReportExcel")
				|| path.equals("/dashboard/passBook/getpassbookStatementReportExcel")
				|| path.equals("/dashboard/serviceReport/getpassbookStatementReportExcel")
				|| path.equals("/dashboard/subMerchantInfo/subMerchantListExcel")
				|| path.equals("/dashboard/v2/getPayinTransactionsStatementReportExcel")
				|| path.equals("/dashboard/merchantSettlement/getMerchantStatementReportExcel")
				|| path.equals("/dashboard/v2/getPgTransactionsStatementReportExcel")
				|| path.equals("/dashboard/v2/getENachTransactionsStatementReportPdf")
				|| path.equals("/dashboard/passBook/getpassbookStatementReportPdf")
				|| path.equals("/dashboard/v2/getPayinTransactionsStatementReportPdf")
				|| path.equals("/dashboard/v2/getPgTransactionsStatementReportPdf")
				|| path.equals("/dashboard/v2/getEkycTransactionsStatementReportExcel")
				|| path.equals("/dashboard/v2/bbpsTransactionHistory")
				|| path.equals("/dashboard/swagger-ui.html")
				|| path.startsWith("/public");
	}

}
