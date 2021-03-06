package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.UnauthorizedException;
import com.example.demo.domain.UserDTO;
import com.example.demo.model.User;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.security.JwtUser;

@RestController
public class AuthenticationController {
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
     @Autowired 
	public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
	 
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@PostMapping(value="/login")
	public ResponseEntity<UserDTO> userLogin(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
			final JwtUser userDetails = (JwtUser)authentication.getPrincipal();
			SecurityContextHolder.getContext().setAuthentication(authentication);
			final String token = jwtTokenUtil.generateToken(userDetails);
			return new ResponseEntity<UserDTO>(new UserDTO(userDetails.getUser(), token), HttpStatus.OK);
			
		}catch (Exception e) {
			throw new UnauthorizedException(e.getMessage());
		}
		
	}

}
