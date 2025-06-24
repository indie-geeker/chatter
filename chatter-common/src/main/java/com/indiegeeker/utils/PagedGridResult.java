package com.indiegeeker.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Desc: 用来返回分页Grid的数据格式
 * Author: wen
 * Date: 2025/6/24
 **/
@Getter
@Setter
public class PagedGridResult {
    private long page;			// 当前页数
	private long total;			// 总页数
    private long records;		// 总记录数
    private List<?> rows;		// 每行显示的内容
}
