package org.bicyclesharing.web.API;


import org.bicyclesharing.entities.Bicycle;
import org.bicyclesharing.entities.Borrow;
import org.bicyclesharing.entities.User;
import org.bicyclesharing.service.BicycleService;
import org.bicyclesharing.service.BorrowService;
import org.bicyclesharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 借车相关api
 * Created by HuiJa on 2017/8/1.
 */
@Controller
public class BorrowApi {
    @Autowired
    BorrowService borrowService;
    @Autowired
    UserService userService;
    @Autowired
    BicycleService bicycleService;


    /**
     * 1.借车开始api,修改单车状况
     */
    @RequestMapping(value="api-borrow-borrowBicycle/{bicycleId}")
    @ResponseBody
    public String borrowBicycle(@PathVariable("bicycleId") Integer bicycleId){
        Bicycle bicycle=bicycleService.getBicycleById(bicycleId);
        if (bicycle.getBicycleStatement()==1||bicycle.getBicycleStatement()==-1){
            bicycle.setBicycleStatement(0);
            bicycleService.editBicycyle(bicycleId,bicycle.getBicycleCurrentX(),bicycle.getBicycleCurrentY(),bicycle.getBicycleStatement());
            return "1";
        }else{
            return "0";
        }

    }
    /**
     * 2.借车结束相关api,添加借车记录,修改用户余额,修改单车状况为1(还有地址)
     * @return
     */
    @RequestMapping(value = "api-borrow-returnBicycle/{stime}/{etime}/{sx}/{sy}/{ex}/{ey}/{cost}/{bicycleId}/{userName}")
    @ResponseBody
    public String returnBicycle( @PathVariable("stime") Date stime,  @PathVariable("etime") Date etime,
                                 @PathVariable("sx") double sx,  @PathVariable("sy") double sy,
                                 @PathVariable("ex") double ex,  @PathVariable("sy") double ey,
                                 @PathVariable("cost") BigDecimal cost,
                                 @PathVariable("bicycleId") Integer bicycleId, @PathVariable("userName") String  userName) {
        User user=userService.getUserByName(userName);
        BigDecimal remaing=user.getUserAccount();
        borrowService.addBorrow(bicycleId,user.getUserId(),stime,etime,sx,sy,ex,ey,cost,remaing.subtract(cost));
        user.setUserAccount(remaing.subtract(cost));
        userService.editUser(user.getUserName(),user.getUserAccount(),user.getUserCredit(),user.getUserCash());
        bicycleService.editBicycyle(bicycleId,ex,ey,1);
        return "1";
    }

    /**
     * 3.查询借车记录api
     */
    @RequestMapping(value = "api-borrow-queryBorrow/{userName}")
    @ResponseBody
    public ArrayList<Borrow> queryBorrow(@PathVariable("userName") String userName){
        ArrayList<Borrow> borrows= (ArrayList<Borrow>) borrowService.getBorrowByUserId(userService.getUserByName(userName).getUserId());
        return borrows;
    }
}
