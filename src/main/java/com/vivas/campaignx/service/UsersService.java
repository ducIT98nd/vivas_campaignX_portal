package com.vivas.campaignx.service;

import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.config.UserPrinciple;
import com.vivas.campaignx.dto.UserDTO;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UsersService {

	protected final Logger logger = LogManager.getLogger(this.getClass().getName());
	private UsersRepository usersRepository;
	private UserRoleRepository userRoleRepository;
	private RoleRepository roleRepository;
	public static final int MAX_FAILED_ATTEMPTS = 5;

	private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000;

	@Autowired
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;

	@Autowired
	public UsersService(UsersRepository usersRepository, UserRoleRepository userRoleRepository,
	                    RoleRepository roleRepository) {
		this.usersRepository = usersRepository;
		this.userRoleRepository = userRoleRepository;
		this.roleRepository = roleRepository;
	}

	public Optional<Users> findUserByUsernameAndStatus(String userName) {
		return usersRepository.findByUsername(userName);
	}

	public void createUser(HashMap<String, String> reqParams, String createdUser) throws AppException {

		Users user = validateUser(reqParams);

		LocalDateTime date = LocalDateTime.now();
		user.setCreatedDate(date);
		user.setModifiedUser(createdUser);
		user.setFailedAttempt(0);
		Long roleId = Long.parseLong(reqParams.get("roleId"));
		user = usersRepository.save(user);

		UserRole userRole = new UserRole();
		Optional<Role> role = roleRepository.findById(roleId);
		if (role.isPresent()){
			userRole.setRole(role.get());
		} else {
			throw new AppException("Vai trò người dùng không hợp lệ");
		}
		userRole.setUser(user);
		userRoleRepository.save(userRole);
		logger.info("save user success");

	}

	private Users validateUser(HashMap<String, String> reqParams) throws AppException {

		String userName = reqParams.get("username");
		String password = reqParams.get("password");
		String confirmPassword = reqParams.get("confirmPassword");
		String name = reqParams.get("name");
		String email = reqParams.get("email");
		Long status = Long.parseLong(reqParams.get("status"));

		if (AppUtils.isStringNullOrEmpty(userName) || userName.length() > 100) {
			throw new AppException("Tên đăng nhập không hợp lệ");
		}

		if (AppUtils.isStringNullOrEmpty(password)) {
			throw new AppException("Mật khẩu không hợp lệ");
		}

		if (AppUtils.isStringNullOrEmpty(name) || name.length() > 100) {
			throw new AppException("Tên người dùng không hợp lệ");
		}

		if (!password.equals(confirmPassword)) {
			throw new AppException("Mật khẩu không trùng khớp");
		}

		Pattern pattern = null;
		pattern = Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher emailMatcher = pattern.matcher(email);
		if (!emailMatcher.matches()) {
			throw new AppException("Email không đúng định dạng");
		}

		if (usersRepository.findByEmailIgnoreCase(email).size() > 0) {
			throw new AppException("Email đã tồn tại trên hệ thống");
		}

		pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$");
		Matcher passwordMatcher = pattern.matcher(password);
		if (!passwordMatcher.matches()) {
			throw new AppException("Mật khẩu không đúng định dạng");
		}

		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		Users user = new Users();
		user.setUsername(userName);
		user.setEmail(email);
		if (status == 1) {
			user.setStatus(1);
		} else
			user.setStatus(0);
		user.setName(name);
		user.setPassword(bcrypt.encode(password));
		user.setNote("0");
		return user;
	}

	private Users validateUserCp(HashMap<String, String> reqParams) throws AppException {

		String userName = reqParams.get("userName");
		String password = reqParams.get("password");
		String confirmPassword = reqParams.get("confirmPassword");
		String status = reqParams.get("status");

		if (AppUtils.isStringNullOrEmpty(userName) || userName.length() > 100) {
			throw new AppException("Tên đăng nhập không hợp lệ");
		}

		if (AppUtils.isStringNullOrEmpty(password)) {
			throw new AppException("Mật khẩu không hợp lệ");
		}

		if (!password.equals(confirmPassword)) {
			throw new AppException("Mật khẩu không trùng khớp");
		}
		
		Pattern pattern = Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

		pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$");
		Matcher passwordMatcher = pattern.matcher(password);
		if (!passwordMatcher.matches()) {
			throw new AppException("Mật khẩu không đúng định dạng");
		}

		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		Users user = new Users();
		user.setUsername(userName);
		if (status != null) {
			if (status.equalsIgnoreCase("on")) {
				user.setStatus(1);
			} else {
				user.setStatus(0);
			}
		} else {
			user.setStatus(0);
		}
		user.setPassword(bcrypt.encode(password));
		return user;
	}

	public Page<UserDTO> findUsers(int pageSize, int currentPage, String username, String email, Long roleId, Long status) {

		Pageable paging = PageRequest.of(currentPage - 1, pageSize);
		return usersRepository.findListUser(paging, username,email, roleId, status);
	}

	public List<UserDTO> findAllUser(String username, String email, Long roleId, Long status) {
		return usersRepository.findAllUser(username,email, roleId, status);
	}

	public Optional<Users> findUserById(Integer userId) {
		return usersRepository.findByUserId(userId);
	}

	public UserDTO findDetailUserById(Long userId){
		return usersRepository.findDetailUser(userId);
	}

	public void updateUser(Users user, HashMap<String, String> reqParams, String currentUser) throws AppException {

		String email = reqParams.get("email");
		String name = reqParams.get("name");
		Long roleId = Long.parseLong(reqParams.get("roleId"));
		Long status = Long.parseLong(reqParams.get("status"));


		Pattern pattern = null;
		pattern = Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher emailMatcher = pattern.matcher(email);
		if (!emailMatcher.matches()) {
			throw new AppException("Email không đúng định dạng");
		}

		if (!user.getEmail().equals(email)) {
			if (usersRepository.findByEmailIgnoreCase(email).size() > 0) {
				throw new AppException("Email đã tồn tại trên hệ thống");
			}
		}
		user.setName(name);
		user.setEmail(email);
		user.setStatus(status.intValue());
		if (status == 1) {
			user.setFailedAttempt(0);
		}
		LocalDateTime date = LocalDateTime.now();
		user.setModifiedUser(currentUser);
		Users u = usersRepository.save(user);
		userRoleRepository.updateByUserID(roleId, (long)u.getUserId());

		logger.info("Cập nhật user thành công");
	}

	public void deleteUser(Users user) {

	//	userRoleRepository.deleteByUser(user);
		usersRepository.delete(user);
		logger.info("Xóa user {} thành công", user.getUsername());
	}

	public void renewPassword(Users user, String currentUser, String password, String passwordConfirm)
			throws AppException {

		validatePassword(password, passwordConfirm);
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		user.setPassword(bcrypt.encode(password));
		LocalDateTime date = LocalDateTime.now();
		user.setModifiedUser(currentUser);
		usersRepository.save(user);
		logger.info("Cấp lại mật khẩu cho người dùng thành công");
	}

	private void validatePassword(String password, String passwordConfirm) throws AppException {

		if (!password.equals(passwordConfirm)) {
			throw new AppException("Mật khẩu không trùng khớp!");
		}
		Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$");
		Matcher matcher = pattern.matcher(password);
		if (!matcher.matches()) {
			throw new AppException("Mật khẩu không đúng định dạng!");
		}
	}

	public boolean validateOtpToken(String token) {
		Optional<Users> usersOptional = usersRepository.findByOtpTokenIgnoreCase(token);
		if (!usersOptional.isPresent()) {
			return false;
		}
		Users users = usersOptional.get();
		return !users.getForgotPasswordDatetime().plusDays(1).isBefore(LocalDateTime.now());
	}

	public Optional<Users> findByTokenIgnoreCase(String otpToken) {
		return usersRepository.findByOtpTokenIgnoreCase(otpToken);
	}

	public Long countUsersByUsernameIgnoreStatusDelete(String username){
		return usersRepository.countUsersByUsernameIgnoreStatusDelete(username.toLowerCase(Locale.ROOT));
	}

	public Long countEmailIgnoreStatusDelete(String email){
		return usersRepository.countEmailIgnoreStatusDelete(email);
	}

	public Long countEmailIgnoreStatus(String email){
		return usersRepository.countEmailIgoreStatus(email);
	}

	public Long countEmailAndIdIgnoreStatusDelete(String email, Long id){
		return usersRepository.countEmailAndIdIgnoreStatusDelete(email, id);
	}

	public UserRole findUserRoledByUserId(Integer userId){
		return userRoleRepository.findUserRoledByUserId(userId);
	}


	public List<Long> getAllLoggedInUsers() {
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<Long> usersNamesList = new ArrayList<>();
		for (Object principal : principals) {

			if (principal instanceof UserPrinciple) {
				usersNamesList.add(((UserPrinciple) principal).getUserId());
			}
		}
		return usersNamesList;
	}

	@Transactional
	public void increaseFailedAttempts(Users user) {
		int newFailAttempts = user.getFailedAttempt() + 1;
		user.setFailedAttempt(newFailAttempts);
		usersRepository.save(user);
	}

	@Transactional
	public void resetFailedAttempts(String userName) {
		usersRepository.updateFailedAttempts(0, userName);
	}

	public void lock(Users user) {
		user.setLockTime(new Date());
		user.setStatus(0);
		usersRepository.save(user);
	}

	public List<Users> findByIsRoot(Boolean isRoot) {
		return usersRepository.findByIsRoot(isRoot);
	}

//	public boolean unlockWhenTimeExpired(Users user) {
//		long lockTimeInMillis = user.getLockTime().getTime();
//		long currentTimeInMillis = System.currentTimeMillis();
//
//		if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
//			user.setAccountNonLocked(true);
//			user.setLockTime(null);
//			user.setFailedAttempt(0);
//			usersRepository.save(user);
//			return true;
//		}
//		return false;
//	}
}