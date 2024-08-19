package dev.fernando.demo.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import dev.fernando.demo.dto.UserUpdateDTO;
import dev.fernando.demo.entities.User;
import dev.fernando.demo.repositories.UserRepository;
import dev.fernando.demo.resources.exceptions.FieldMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletRequest request;

	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		Optional<User> user = userRepository.findByEmail(dto.getEmail());
		var atributos = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		var userId = Long.valueOf(atributos.get("id"));
		if (user.isPresent() && !userId.equals(user.get().getId())) {
            list.add(new FieldMessage("Email", "Email %s já existe".formatted(dto.getEmail())));
        }
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}