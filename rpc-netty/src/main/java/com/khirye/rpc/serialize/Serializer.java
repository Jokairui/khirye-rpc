package com.khirye.rpc.serialize;

public interface Serializer<T> {

    /**
     * 计算序列化后对象的长度，主要用于申请存放序列化数据的字节数组
     * @param entry the object which is serialized
     * @return the bytes length
     */
    int size(T entry);


    /**
     * 序列化对象，将给定的对象序列化成byte数组
     * @param entry 待序列化的对象
     * @param bytes 存放序列化数据的byte数组
     * @param offset 从这个位置写入序列化数据
     * @param length 对象序列化后的长度，也就是{@link Serializer#size(java.lang.Object)}方法的返回值
     */
    void serialize(T entry, byte[] bytes, int offset, int length) ;

    /**
     * 反序列化
     * @param bytes 存放序列化数据的byte数组
     * @param offset 从这个位置开始反序列化对象
     * @param length 对象序列化后的长度
     * @return
     */
    T parse(byte[] bytes, int offset, int length);

    /**
     * 用一个字节标志对象类型， 每种类型的数据应该具有不同的类型值
     * @return
     */
    byte type();

    /**
     * 返回序列化对象类型的Class对象
     * @return
     */
    Class<T> getSerializeClass();
}
