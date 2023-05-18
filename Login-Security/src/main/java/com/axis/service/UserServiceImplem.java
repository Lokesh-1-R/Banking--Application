package com.axis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.axis.user.User;
import com.axis.userrepository.UserRepository;

@Service
public class UserServiceImplem {
	
	@Autowired
	private UserRepository userRepo;
	public List<User>getAllUsers(){
		return userRepo.findAll();
	}
	public User getUserByID(int id) {
		return userRepo.findById(id).get();
	}
}
