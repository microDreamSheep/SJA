package indi.midreamsheep.sja.processor.pojo.check;

import indi.midreamsheep.sja.annotation.pojo.check.Check;
import indi.midreamsheep.sja.annotation.pojo.check.NotCheck;
import indi.midreamsheep.sja.inter.pojo.check.CheckerInter;
import indi.midreamsheep.sja.processor.pojo.check.entity.FiledExpression;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;

public class CheckUtil {
    public static boolean check(Element element) {
        //判断是不是类
        boolean isClass = element.getKind()== ElementKind.CLASS;

        //判断是否实现了CheckerInter接口
        boolean isImpl = false;
        for (Class<?> anInterface : element.getClass().getInterfaces()) {
            if (anInterface!= CheckerInter.class){
                continue;
            }
            isImpl = true;
        }

        return isClass && isImpl;
    }

}
