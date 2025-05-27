//package com.fidypay.config;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.fidypay.encryption.Encryption;
//import com.fidypay.entity.Merchants;
//import com.fidypay.repo.MerchantsRepository;
//
//@Configuration
//@EnableWebSecurity
//public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
//	
//	@Autowired
//	MerchantsRepository merchantsRepository;
//	
//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf().disable()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/modes/paymentLink").permitAll()
//                .antMatchers(HttpMethod.OPTIONS, "/modes/success").permitAll()
//                .antMatchers(HttpMethod.OPTIONS, "/modes/upi").permitAll()
//
//                .antMatchers("/swagger-resources/*", "*.html", "/api/v1/swagger.json").hasRole("SWAGGER")
//                .anyRequest().authenticated()
//                .and().httpBasic();
//
//        //    httpSecurity.httpBasic().and().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "139.59.64.40:8081/bbps/upiPayment/home").permitAll();
//
//    }
//    
//    
//   
//    
//    @Autowired
//	public void configureGlobal(AuthenticationManagerBuilder authentication) throws Exception {
//
//	 List<Merchants> list = merchantsRepository.findAll();
//		
//		for(Merchants merchants:list) {
//			
//			String uName=merchants.getMerchantFirstname();
//			String uPassword=merchants.getMerchantPassword();
//			
//			uName=Encryption.decString(uName);
//			uPassword=Encryption.decString(uPassword);
//		
//		
//		authentication.inMemoryAuthentication().withUser(uName).password(passwordEncoder().encode(uPassword))
//				.authorities("ROLE_USER");
//		
//		
//		
//		}
//	}
//
//    
//    
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/pg/**");
//        web.ignoring().antMatchers("/modes/paymentLink/**");
//        web.ignoring().antMatchers("/modes/success/**");
//        web.ignoring().antMatchers("/modes/upi/**");
//
//        
//    }
//    
//    
//}
