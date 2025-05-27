package com.fidypay.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private LoginKeyFilter loginKeyFilter;

	public SecurityConfig(LoginKeyFilter loginKeyFilter) {
		this.loginKeyFilter = loginKeyFilter;
	}

//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.cors(withDefaults()).csrf(csrf -> csrf.disable())
//				.authorizeRequests(requests -> requests.antMatchers("/accountDetails/login-merchant/**").permitAll()
//						.anyRequest().authenticated())
//				.addFilterBefore(loginKeyFilter, UsernamePasswordAuthenticationFilter.class); // Add custom filter
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
      http.cors(withDefaults()).csrf(csrf -> csrf.disable())
              .authorizeRequests(requests -> requests
                      .antMatchers("/accountDetails/login**", "/accountDetails/checkMerchant**", "/accountDetails/getMerchantServiceDetails**", "/accountDetails/forgetMerchantPassowrd**", "/accountDetails/login-merchant**", "/accountDetails/login-merchant-otp**", "/accountDetails/login-merchant-sandbox**", 
                    		  "/accountDetails/login-pwa/**", "/accountDetails/verify-login-otp/**", "/accountDetails/verifyMerchantEmail**").permitAll()
                      .antMatchers("/global/citiesList**", "/global/citiesListByStateName**", "/global/country_list**", "/global/statesList**").permitAll()
                      .antMatchers("/api/kyc/**").permitAll()
                      .antMatchers("/merchantService/forgetPassword**", "/merchantService/getotp**", "/merchantService/merchantDeactive**", "/merchantService/merchantLogin**", "/merchantService/merchantRegister**", "/merchantService/v2/merchantRegister**").permitAll()
                      .antMatchers("/subMerchantInfo/checkLimit/**").permitAll()
                      .antMatchers("/subMerchantInfo/findByMobile/**").permitAll()
                      .antMatchers("/subMerchantInfo/cbank/sub-merchant/**").permitAll()
                      .antMatchers("/subMerchantInfo/findByMobileNumber/**").permitAll()
                      .antMatchers("/subMerchantInfo/getCitiesListByStateCode/**").permitAll()
                      .antMatchers("/subMerchantInfo/getCitiesListByStateName/**").permitAll()
                      .antMatchers("/subMerchantInfo/getSubMerchantDetails/**").permitAll()
                      .antMatchers("/subMerchantInfo/getSubMerchantDetailsSheet/**").permitAll()
                      .antMatchers("/subMerchantInfo/subMerchantListExcelName/**").permitAll()
                      .antMatchers("/subMerchantInfo/updateBySubMerchantId/**").permitAll()
                      .antMatchers("/v2/updateTrxnIdentifierOnBBPSTrxnDeyails/**").permitAll()
                      .antMatchers("/logOut/**").permitAll() .antMatchers("/AddFloatByAdmin/POST/**").permitAll()
                      .antMatchers("/addMerchantInfoByMerchantId/**").permitAll()
                      .antMatchers("/saveMerchantInfo/**").permitAll()
                      .antMatchers("/CreateMerchantByAdmin/POST/**").permitAll()
                      .antMatchers("/serviceReport/getStatus/**").permitAll()
                      .antMatchers("/merchantUser/addRecordByEmail/**").permitAll()
                      .antMatchers("/merchantUser/addRecords/**").permitAll()
                      .antMatchers("/pwa/getSettlementList/**").permitAll()
                      .antMatchers("/pwa/login/**").permitAll()
                      .antMatchers("/checkEmailId/**").permitAll()
                      .antMatchers("/checkMobileNo/**").permitAll()
                      .antMatchers("/checkPanNumber/**").permitAll()
                      .antMatchers("/getBussinessTypeList/**").permitAll()
                      .antMatchers("/getMCCDetailsList/**").permitAll()
                      .antMatchers("/loginWithOTP/**").permitAll()
                      .antMatchers("/V2/merchantRegister/**").permitAll()
                      .antMatchers("/sub-merchant-temp/*/verifySubMerchant/*").permitAll()
                      .antMatchers("/sub-merchant-temp/*/otpVerification/*").permitAll()
                      .antMatchers("/sub-merchant-temp/findByToken/**").permitAll()
                      .antMatchers("/sub-merchant-temp/findByTokenNew/**").permitAll()
                      .antMatchers("/sub-merchant-temp/resend-notification/**").permitAll()
                      .antMatchers("/sub-merchant-temp/save-merchant-submerchant-Info/**").permitAll()
                      .antMatchers("/sub-merchant-temp/save-merchant-submerchant-Info-date/*/to/*").permitAll()
                      .antMatchers("/sub-merchant-temp/save-merchant-submerchant-tempid/**").permitAll()
                      .antMatchers("/sub-merchant-temp/sendOTPPhone/**").permitAll()
                      .antMatchers("/sub-merchant-temp/update-details/**").permitAll()
                      .antMatchers("/upload-banner/**").permitAll()
                      .antMatchers("/merchantUser/register/**").permitAll()
                      .antMatchers("/v2/getBBPSTransactionsStatementReportExcel/**").permitAll()
                      .antMatchers("/v2/bbpsCommissionsListReportExcel/**").permitAll()
                      .antMatchers("/v2/getPayoutTransactionsStatementReportExcel/**").permitAll()
                      .antMatchers("/getENachSettlementReportExcel/**").permitAll()
                      .antMatchers("/v2/getENachTransactionsStatementReportExcel/**").permitAll()
                      .antMatchers("/serviceReport/getpassbookStatementReportExcel/**").permitAll()
                      .antMatchers("/passBook/getpassbookStatementReportExcel/**").permitAll()
                      .antMatchers("/subMerchantInfo/subMerchantListExcel/**").permitAll()
                      .antMatchers("/v2/getPayinTransactionsStatementReportExcel/**").permitAll()
                      .antMatchers("/merchantSettlement/getMerchantStatementReportExcel/**").permitAll()
                      .antMatchers("/v2/getPayinTransactionsStatementReportExcel/**").permitAll()
                      .antMatchers("/v2/getPgTransactionsStatementReportExcel/**").permitAll()
                      .antMatchers("/v2/getENachTransactionsStatementReportPdf/**").permitAll()
                      .antMatchers("/passBook/getpassbookStatementReportPdf/**").permitAll()
                      .antMatchers("/v2/getPayinTransactionsStatementReportPdf/**").permitAll()
                      .antMatchers("/v2/getPgTransactionsStatementReportPdf/**").permitAll()
                      .antMatchers("/v2/getEkycTransactionsStatementReportExcel/**").permitAll()
                      .antMatchers("/v2/bbpsTransactionHistory/**").permitAll()
                      .antMatchers("/accountDetails/*/mobileNoVerification/*").permitAll()
                      .antMatchers("/accountDetails/*/otpVerification/*").permitAll()
                      .antMatchers("/accountDetails/mobileNoLogin/**").permitAll()
                      .antMatchers("/swagger-ui.html").permitAll()
                      .antMatchers("/webjars/**").permitAll()
                    
                     
                           
                      .anyRequest().authenticated())
              .addFilterBefore(loginKeyFilter, UsernamePasswordAuthenticationFilter.class); // Add custom filter
  }
	
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(Arrays.asList("*")); // Frontend origin
		corsConfiguration.setAllowedMethods(Arrays.asList("*"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
		corsConfiguration.setAllowCredentials(false); // Enable cookies/sessions sharing

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		return new CorsFilter(source);
	}

}
