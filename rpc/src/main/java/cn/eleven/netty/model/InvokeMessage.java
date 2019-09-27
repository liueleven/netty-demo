package cn.eleven.netty.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: 消费者发送给服务端的调用信息
 * @date: 2019-09-26 22:12
 * @author: 十一
 */
@Data
@Builder
@NoArgsConstructor
public class InvokeMessage implements Serializable {

    /**
     * 服务名称
     */
    private String className;

    /**
     * 调用方法名称
     */
    private String methodName;

    /**
     * 方法参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 方法参数列值
     */
    private Object[] paramValues;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }
}
