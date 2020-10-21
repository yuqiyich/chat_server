/**
 * 主要用于推荐点查询与分析
 *  查询步骤
 *  1.根据经纬度输入
 *       ||
 *       ||
 *       \/
 * IPointQueryStrategy 查询策略 ，可以多个策略多线程并行进行，对结果进行交集或者并集
 *       ||
 *       ||
 *       \/
 *  IPointFilter  点过滤策略,多个策略串行过滤
 *       ||
 *       ||
 *       \/
 *     结果输出
 */
package com.ruqi.appserver.ruqi.geomesa.recommendpoint;
