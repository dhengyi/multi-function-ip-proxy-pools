package ip.proxy.pool.utilclass;

/**
 * @author dhengyi
 * @create 2019/05/05 23:00
 * @description 参数校验工具类
 */

public class ParamValidateUtil {

    public static boolean validateRange(Integer min, Integer max, Integer number) {
        return !(min == null || max == null || number == null) && !(number < min || number > max);
    }
}
