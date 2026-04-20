package com.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.BusinessException;
import com.student.dto.ChangePasswordDTO;
import com.student.dto.LoginDTO;
import com.student.dto.ProfileUpdateDTO;
import com.student.dto.SysUserDTO;
import com.student.entity.SysUser;
import com.student.mapper.SysUserMapper;
import com.student.query.SysUserQuery;
import com.student.service.SysUserService;
import com.student.utils.JwtUtils;
import com.student.vo.LoginVO;
import com.student.vo.SysUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public IPage<SysUserVO> getPageList(SysUserQuery query) {
        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, query.getKeyword())
                    .or().like(SysUser::getRealName, query.getKeyword()));
        }
        if (query.getRole() != null) {
            wrapper.eq(SysUser::getRole, query.getRole());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> data = sysUserMapper.selectPage(page, wrapper);
        return data.convert(this::toVO);
    }

    @Override
    public SysUserVO getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return toVO(user);
    }

    @Override
    public void add(SysUserDTO dto) {
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) throw new BusinessException("用户名已存在");

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "123456"));
        if (user.getStatus() == null) user.setStatus(1);
        sysUserMapper.insert(user);
    }

    @Override
    public void update(Long id, SysUserDTO dto) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");

        if (!user.getUsername().equals(dto.getUsername())) {
            Long count = sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
            if (count > 0) throw new BusinessException("用户名已存在");
        }

        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        sysUserMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        if (user.getRole() == 1) throw new BusinessException("不能删除管理员账号");
        sysUserMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(passwordEncoder.encode("123456"));
        sysUserMapper.updateById(user);
    }

    @Override
    public void toggleStatus(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        if (user.getRole() == 1) throw new BusinessException("不能禁用管理员账号");
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        sysUserMapper.updateById(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO dto, Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        sysUserMapper.updateById(user);
    }

    @Override
    public SysUserVO getProfile(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return toVO(user);
    }

    @Override
    public void updateProfile(Long userId, ProfileUpdateDTO dto) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        sysUserMapper.updateById(user);
    }

    private SysUserVO toVO(SysUser user) {
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setRoleName(getRoleName(user.getRole()));
        return vo;
    }

    private String getRoleName(Integer role) {
        return switch (role) {
            case 1 -> "管理员";
            case 2 -> "教师";
            case 3 -> "学生";
            default -> "未知";
        };
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, loginDTO.getUsername()));

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRole(user.getRole());
        userInfo.setRoleName(getRoleName(user.getRole()));
        userInfo.setAvatar(user.getAvatar());
        loginVO.setUserInfo(userInfo);

        return loginVO;
    }

    @Override
    public LoginVO.UserInfo getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRole(user.getRole());
        userInfo.setRoleName(getRoleName(user.getRole()));
        userInfo.setAvatar(user.getAvatar());
        return userInfo;
    }

    @Override
    public void logout() {
    }

    @Override
    public java.util.Map<Integer, Long> getRoleCounts() {
        java.util.Map<Integer, Long> counts = new java.util.HashMap<>();
        for (int role = 1; role <= 3; role++) {
            counts.put(role, sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, role)));
        }
        return counts;
    }
}
