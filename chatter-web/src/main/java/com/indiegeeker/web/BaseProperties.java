package com.indiegeeker.web;



/**
 * Desc:
 * Author: wen
 * Date: 2025/6/24
 **/
public class BaseProperties {

    public static final String TOKEN_USER_PREFIX = "app";       // app端的用户token前缀
    public static final String TOKEN_SAAS_PREFIX = "saas";      // 企业saas平台的用户token前缀
    public static final String TOKEN_ADMIN_PREFIX = "admin";    // 运营管理平台的用户token前缀

    public static final Integer PAGE_START = 1;
    public static final Integer PAGE_SIZE = 10;

    public static final String MOBILE_SMSCODE = "mobile:smscode";


    /**
     * 适用于page-helper
     * @param list
     * @param page
     * @return
     */
    // public PagedGridResult setterPagedGridHelper(List<?> list,
    //                                        Integer page) {
    //     PageInfo<?> pageList = new PageInfo<>(list);
    //     PagedGridResult gridResult = new PagedGridResult();
    //     gridResult.setRows(list);
    //     gridResult.setPage(page);
    //     gridResult.setRecords(pageList.getTotal());
    //     gridResult.setTotal(pageList.getPages());
    //     return gridResult;
    // }

    /**
     * 适用于 mybatis-plus
     * @param pageInfo
     * @return
     */
//    public PagedGridResult setterPagedGridPlus(Page<?> pageInfo) {
//
//        //获取分页数据
//        List<?> list = pageInfo.getRecords();
//        // list.forEach(System.out::println);
//        System.out.println("当前页：" + pageInfo.getCurrent());
//        System.out.println("每页显示的条数：" + pageInfo.getSize());
//        System.out.println("总记录数：" + pageInfo.getTotal());
//        System.out.println("总页数：" + pageInfo.getPages());
//        System.out.println("是否有上一页：" + pageInfo.hasPrevious());
//        System.out.println("是否有下一页：" + pageInfo.hasNext());
//
//        PagedGridResult gridResult = new PagedGridResult();
//        gridResult.setRows(list);
//        gridResult.setPage(pageInfo.getCurrent());
//        gridResult.setRecords(pageInfo.getTotal());
//        gridResult.setTotal(pageInfo.getPages());
//        return gridResult;
//    }
}
