package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.student.dto.ChangePasswordDTO;
import com.student.dto.LoginDTO;
import com.student.dto.ProfileUpdateDTO;
import com.student.dto.SysUserDTO;
import com.student.query.SysUserQuery;
import com.student.vo.LoginVO;
import com.student.vo.SysUserVO;

public interface SysUserService {

    IPage<SysUserVO> getPageList(SysUserQuery query);

    SysUserVO getById(Long id);

    void add(SysUserDTO dto);

    void update(Long id, SysUserDTO dto);

    void delete(Long id);

    void resetPassword(Long id);

    void toggleStatus(Long id);

    void changePassword(ChangePasswordDTO dto, Long userId);

    SysUserVO getProfile(Long userId);

    void updateProfile(Long userId, ProfileUpdateDTO dto);

    LoginVO login(LoginDTO loginDTO);

    LoginVO.UserInfo getUserInfo(Long userId);

    void logout();

    java.util.Map<Integer, Long> getRoleCounts();
}
