package com.zzt.domain.dto;

import com.zzt.domain.Setmeal;
import com.zzt.domain.SetmealDish;
import lombok.Data;
import java.util.List;

/*setmeal+setmealDishes集合*/
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
