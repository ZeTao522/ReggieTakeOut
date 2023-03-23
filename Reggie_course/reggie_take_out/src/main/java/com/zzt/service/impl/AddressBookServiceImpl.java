package com.zzt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.domain.AddressBook;
import com.zzt.mapper.AddressBookMapper;
import com.zzt.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
