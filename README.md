# 天机学堂 (TianJi Xuetang)

## 技术栈

### 核心技术

- **Java 11** - 主要开发语言
- **Spring Boot 2.7.2** - 应用框架
- **Spring Cloud 2021.0.3** - 微服务框架
- **Spring Cloud Alibaba 2021.0.1.0** - 阿里云微服务组件
- **MyBatis Plus 3.4.3** - ORM 框架
- **MySQL 8.0.23** - 主数据库
- **Redis** - 缓存和会话存储
- **Nacos** - 服务注册与配置中心
- **RabbitMQ** - 消息队列
- **Redisson 3.13.6** - 分布式锁
- **Elasticsearch 7.12.1** - 搜索引擎
- **XXL-Job 2.3.1** - 分布式任务调度
- **Seata 1.5.1** - 分布式事务

### 第三方服务集成

- **阿里云 OSS** - 对象存储
- **腾讯云 COS** - 对象存储
- **腾讯云 VOD** - 视频点播
- **微信支付** - 支付服务
- **支付宝** - 支付服务
- **腾讯云短信** - 短信服务

### 开发工具

- **Lombok** - 代码简化
- **Hutool 5.7.17** - 工具类库
- **Knife4j 3.0.3** - API 文档
- **Swagger** - API 文档

## 项目架构

### 整体架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用      │    │   管理后台      │    │   移动端        │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │       网关层              │
                    │   tj-gateway              │
                    └─────────────┬─────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
┌───────▼────────┐    ┌──────────▼──────────┐    ┌────────▼────────┐
│   认证授权      │    │     业务服务        │    │    基础服务     │
│  tj-auth       │    │                     │    │                 │
└────────────────┘    │  tj-user            │    │  tj-common      │
                      │  tj-course          │    │  tj-api         │
                      │  tj-trade           │    │  tj-message     │
                      │  tj-pay             │    │  tj-media       │
                      │  tj-exam            │    │  tj-search      │
                      │  tj-learning        │    │  tj-data        │
                      └─────────────────────┘    └─────────────────┘
```

### 1. 网关服务 (tj-gateway) - 统一入口

#### 模块结构

```
tj-gateway/
├── config/          # 网关配置
├── filter/          # 过滤器
├── exception/       # 异常处理
└── swagger/         # API文档聚合
```

#### 核心功能实现

- **路由转发**: 根据请求路径转发到对应的微服务
- **认证鉴权**: 通过过滤器验证 JWT Token
- **限流熔断**: 使用 Sentinel 进行流量控制
- **跨域处理**: 配置 CORS 策略
- **Swagger 聚合**: 统一 API 文档入口

#### 关键类说明

- GatewayApplication: 网关启动类
- AuthFilter: 认证过滤器，验证用户登录状态
   - 实现用户身份识别（Token 解析）；
   - 实现用户信息透传（写入请求头）；
   - 实现接口级别的权限控制；
   - 配合全局异常处理器，返回标准化错误响应。
- RequestIdRelayFilter: 请求UUID生成过滤器
   - 用于在请求进入网关时生成并传递 唯一请求标识（requestId） 和 来源标识（from）。
- GatewayExceptionHandler:网关服务同一异常处理
   - 对不同类型的异常进行分类处理；
   - 返回标准化的 JSON 错误响应；
   - 记录异常日志，便于排查问题；
   - 支持 requestId透传，便于链路追踪，requestId 就用户的一个请求的UUID，是用来做 全链路追踪和日志关联 的关键标识。
- SwaggerConfig: API 文档配置

---

### 2. 认证授权服务 (tj-auth) - 安全中心

#### 模块结构

```
tj-auth/
├── tj-auth-service/     # 认证服务实现
│   ├── controller/      # 控制器
│   ├── service/         # 业务逻辑
│   ├── mapper/          # 数据访问
│   ├── domain/          # 实体类
│   ├── config/          # 配置类
│   ├── constants/       # 常量定义
│   ├── task/            # 定时任务
│   └── util/            # 工具类
├── tj-auth-common/      # 公共模块
├── tj-auth-gateway-sdk/ # 网关SDK
└── tj-auth-resource-sdk/ # 资源SDK
```

#### 核心功能实现

- **JWT Token 管理**: 生成、验证、刷新 Token
- **权限控制**: 基于角色的访问控制(RBAC)
- **登录记录**: 记录用户登录日志
- **会话管理**: 管理用户会话状态

#### 关键类说明

- `AuthApplication`: 认证服务启动类
- `LoginController`: 登录接口控制器
- `JwtUtils`: JWT 工具类，处理 Token 生成和验证
- `PermissionService`: 权限服务，验证用户权限
- `LoginRecordMapper`: 登录记录数据访问层

---

### 3. 用户服务 (tj-user) - 用户管理中心

#### 模块结构

```
tj-user/
├── controller/      # 控制器层
│   ├── UserController.java      # 用户管理接口
│   └── StudentController.java   # 学生管理接口
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── UserServiceImpl.java     # 用户服务实现
│   │   ├── CodeServiceImpl.java     # 验证码服务实现
│   │   └── StudentServiceImpl.java  # 学生服务实现
│   ├── IUserService.java
│   ├── ICodeService.java
│   └── IStudentService.java
├── mapper/          # 数据访问层
│   ├── UserMapper.java
│   └── UserDetailMapper.java
├── domain/          # 实体类
│   ├── po/          # 持久化对象
│   │   ├── User.java
│   │   └── UserDetail.java
│   ├── dto/         # 数据传输对象
│   └── vo/          # 视图对象
├── enums/           # 枚举类
│   └── UserStatus.java
├── constants/       # 常量定义
│   ├── UserConstants.java
│   └── UserErrorInfo.java
└── config/          # 配置类
    └── SecurityConfig.java
```

#### 核心功能实现

##### 用户注册登录流程

1. **用户注册**:

    - `StudentController.registerStudent()` - 学生注册
    - `CodeServiceImpl.sendVerifyCode()` - 发送短信验证码
    - `UserServiceImpl.addUserByPhone()` - 创建用户账号

2. **用户登录**:

    - `UserController.queryUserDetail()` - 用户登录验证
    - `UserServiceImpl.loginByPw()` - 密码登录
    - `UserServiceImpl.loginByVerifyCode()` - 验证码登录

3. **密码管理**:
    - `UserServiceImpl.updatePasswordByPhone()` - 手机号修改密码
    - `UserServiceImpl.updateUserWithPassword()` - 修改密码

#### 关键类说明

- `UserServiceImpl`: 用户服务核心实现，处理用户 CRUD 操作
- `CodeServiceImpl`: 验证码服务，负责短信验证码的发送和验证
- `UserMapper`: 用户数据访问层，处理用户数据持久化
- `SecurityConfig`: 安全配置，配置密码加密器
- `UserConstants`: 用户相关常量定义

---

### 4. 课程服务 (tj-course) - 课程管理中心

#### 模块结构

```
tj-course/
├── controller/      # 控制器层
│   ├── CourseController.java      # 课程管理接口
│   ├── CourseInfoController.java  # 课程信息接口
│   └── CategoryController.java    # 分类管理接口
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── CourseServiceImpl.java      # 课程服务实现
│   │   ├── CourseDraftServiceImpl.java # 课程草稿服务
│   │   ├── CategoryServiceImpl.java    # 分类服务实现
│   │   └── CourseCatalogueServiceImpl.java # 目录服务实现
│   ├── ICourseService.java
│   ├── ICourseDraftService.java
│   └── ICategoryService.java
├── mapper/          # 数据访问层
│   ├── CourseMapper.java
│   ├── CourseDraftMapper.java
│   └── CategoryMapper.java
├── domain/          # 实体类
│   ├── po/          # 持久化对象
│   │   ├── Course.java
│   │   ├── CourseDraft.java
│   │   └── Category.java
│   ├── dto/         # 数据传输对象
│   └── vo/          # 视图对象
├── constants/       # 常量定义
│   ├── CourseStatus.java
│   └── CourseErrorInfo.java
├── handler/         # 定时任务处理器
│   └── CourseJobHandler.java
├── utils/           # 工具类
└── properties/      # 配置属性
```

#### 核心功能实现

##### 课程管理流程

1. **课程创建**:

    - `CourseController.save()` - 保存课程基本信息
    - `CourseDraftServiceImpl.save()` - 保存课程草稿
    - `CategoryService.checkCategory()` - 验证课程分类

2. **课程上架**:

    - `CourseController.upShelf()` - 课程上架
    - `CourseDraftServiceImpl.checkBeforeUpShelf()` - 上架前校验
    - `CourseServiceImpl.updateStatus()` - 更新课程状态

3. **课程目录管理**:

    - `CourseCatalogueService.queryCourseCatalogues()` - 查询课程目录
    - `CourseCatalogueService.save()` - 保存目录信息

4. **课程搜索**:
    - `CourseServiceImpl.queryForPage()` - 分页查询课程
    - `CourseServiceImpl.getSimpleInfoList()` - 获取课程简单信息

#### 关键类说明

- `CourseServiceImpl`: 课程服务核心实现，处理课程 CRUD 操作
- `CourseDraftServiceImpl`: 课程草稿服务，处理课程编辑状态
- `CategoryServiceImpl`: 分类服务，管理课程分类体系
- `CourseJobHandler`: 定时任务处理器，处理课程自动完结等任务
- `CourseMapper`: 课程数据访问层

---

### 5. 交易服务 (tj-trade) - 订单交易中心

#### 模块结构

```
tj-trade/
├── controller/      # 控制器层
│   ├── OrderController.java      # 订单管理接口
│   ├── CartController.java       # 购物车接口
│   └── PayController.java        # 支付接口
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── OrderServiceImpl.java     # 订单服务实现
│   │   ├── CartServiceImpl.java      # 购物车服务实现
│   │   └── PayServiceImpl.java       # 支付服务实现
│   ├── IOrderService.java
│   ├── ICartService.java
│   └── IPayService.java
├── mapper/          # 数据访问层
│   ├── OrderMapper.java
│   ├── OrderDetailMapper.java
│   └── CartMapper.java
├── domain/          # 实体类
│   ├── po/          # 持久化对象
│   │   ├── Order.java
│   │   ├── OrderDetail.java
│   │   └── Cart.java
│   ├── dto/         # 数据传输对象
│   └── vo/          # 视图对象
├── constants/       # 常量定义
│   ├── OrderStatus.java
│   └── TradeErrorInfo.java
├── handler/         # 消息处理器
│   └── PayMessageHandler.java
└── config/          # 配置类
    └── TradeProperties.java
```

#### 核心功能实现

##### 订单交易流程

1. **购物车管理**:

    - `CartController.addToCart()` - 添加商品到购物车
    - `CartServiceImpl.queryMyCart()` - 查询购物车
    - `CartServiceImpl.deleteCart()` - 删除购物车商品

2. **订单创建**:

    - `OrderController.placeOrder()` - 创建订单
    - `OrderServiceImpl.placeOrder()` - 订单业务逻辑
    - `OrderServiceImpl.saveOrderAndDetails()` - 保存订单和详情

3. **支付处理**:

    - `PayController.applyPayOrder()` - 申请支付
    - `PayServiceImpl.applyPayOrder()` - 支付业务逻辑
    - `PayMessageHandler.listenPaySuccess()` - 监听支付成功消息

4. **订单状态管理**:
    - `OrderServiceImpl.handlePaySuccess()` - 处理支付成功
    - `OrderServiceImpl.cancelOrder()` - 取消订单

#### 关键类说明

- `OrderServiceImpl`: 订单服务核心实现，处理订单全生命周期
- `PayServiceImpl`: 支付服务，集成第三方支付
- `CartServiceImpl`: 购物车服务，管理用户购物车
- `PayMessageHandler`: 支付消息处理器，处理支付回调
- `OrderMapper`: 订单数据访问层

---

### 6. 支付服务 (tj-pay) - 支付处理中心

#### 模块结构

```
tj-pay/
├── tj-pay-service/      # 支付服务实现
│   ├── controller/      # 控制器层
│   │   ├── PayOrderController.java    # 支付订单接口
│   │   └── PayChannelController.java  # 支付渠道接口
│   ├── service/         # 业务逻辑层
│   │   ├── impl/
│   │   │   ├── PayOrderServiceImpl.java    # 支付订单服务
│   │   │   ├── NotifyServiceImpl.java      # 通知服务
│   │   │   └── PayChannelServiceImpl.java  # 支付渠道服务
│   │   ├── IPayOrderService.java
│   │   └── INotifyService.java
│   ├── mapper/          # 数据访问层
│   ├── domain/          # 实体类
│   ├── third/           # 第三方支付集成
│   │   ├── wx/          # 微信支付
│   │   └── ali/         # 支付宝
│   └── config/          # 配置类
├── tj-pay-api/          # API接口定义
└── tj-pay-domain/       # 领域模型
```

#### 核心功能实现

##### 支付处理流程

1. **支付申请**:

    - `PayOrderController.applyPayOrder()` - 申请支付订单
    - `PayOrderServiceImpl.applyPayOrder()` - 创建支付单
    - `PayOrderServiceImpl.checkIdempotent()` - 幂等性校验

2. **第三方支付集成**:

    - `WxPayService.createPrepayOrder()` - 微信支付预下单
    - `AliPayService.createPrepayOrder()` - 支付宝预下单

3. **支付回调处理**:

    - `NotifyServiceImpl.handleWxPayNotify()` - 处理微信支付回调
    - `NotifyServiceImpl.handleAliPayNotify()` - 处理支付宝回调

4. **支付状态查询**:
    - `PayOrderServiceImpl.checkPayOrder()` - 定时查询支付状态
    - `PayOrderServiceImpl.queryPayResult()` - 查询支付结果

#### 关键类说明

- `PayOrderServiceImpl`: 支付订单服务，处理支付单全生命周期
- `NotifyServiceImpl`: 通知服务，处理第三方支付回调
- `WxPayService`: 微信支付服务，集成微信支付 API
- `AliPayService`: 支付宝服务，集成支付宝 API
- `PayOrderMapper`: 支付订单数据访问层

---

### 7. 学习服务 (tj-learning) - 学习跟踪中心

#### 模块结构

```
tj-learning/
├── controller/      # 控制器层
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── LearningRecordServiceImpl.java  # 学习记录服务
│   │   └── LearningLessonServiceImpl.java  # 学习课程服务
│   ├── ILearningRecordService.java
│   └── ILearningLessonService.java
├── mapper/          # 数据访问层
├── domain/          # 实体类
│   ├── po/          # 持久化对象
│   │   ├── LearningRecord.java
│   │   └── LearningLesson.java
│   ├── dto/         # 数据传输对象
│   └── vo/          # 视图对象
└── handler/         # 消息处理器
    └── OrderMessageHandler.java
```

#### 核心功能实现

##### 学习跟踪流程

1. **学习记录**:

    - `LearningRecordService.saveRecord()` - 保存学习记录
    - `LearningRecordService.queryMyRecords()` - 查询学习记录

2. **学习进度**:

    - `LearningLessonService.queryMyLessons()` - 查询我的课程
    - `LearningLessonService.updateProgress()` - 更新学习进度

3. **学习统计**:
    - `LearningRecordService.getStatistics()` - 获取学习统计
    - `LearningLessonService.getLearningReport()` - 生成学习报告

#### 关键类说明

- `LearningRecordServiceImpl`: 学习记录服务，跟踪用户学习行为
- `LearningLessonServiceImpl`: 学习课程服务，管理用户课程学习
- `OrderMessageHandler`: 订单消息处理器，处理订单支付成功后的学习解锁
- `LearningRecordMapper`: 学习记录数据访问层

---

### 8. 考试服务 (tj-exam) - 考试管理中心

#### 模块结构

```
tj-exam/
├── controller/      # 控制器层
│   ├── QuestionController.java      # 题目管理接口
│   └── QuestionBizController.java   # 题目业务关联接口
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── QuestionServiceImpl.java     # 题目服务实现
│   │   ├── QuestionDetailServiceImpl.java # 题目详情服务
│   │   └── QuestionBizServiceImpl.java  # 题目业务关联服务
│   ├── IQuestionService.java
│   ├── IQuestionDetailService.java
│   └── IQuestionBizService.java
├── mapper/          # 数据访问层
│   ├── QuestionMapper.java
│   ├── QuestionDetailMapper.java
│   └── QuestionBizMapper.java
├── domain/          # 实体类
│   ├── po/          # 持久化对象
│   │   ├── Question.java
│   │   ├── QuestionDetail.java
│   │   └── QuestionBiz.java
│   ├── dto/         # 数据传输对象
│   └── vo/          # 视图对象
├── constants/       # 常量定义
│   ├── QuestionType.java
│   └── ExamErrorInfo.java
└── query/           # 查询对象
    └── QuestionPageQuery.java
```

#### 核心功能实现

##### 考试管理流程

1. **题目管理**:

    - `QuestionController.addQuestion()` - 新增题目
    - `QuestionServiceImpl.addQuestion()` - 题目业务逻辑
    - `QuestionDetailServiceImpl.save()` - 保存题目详情

2. **题目分类**:

    - `QuestionServiceImpl.queryQuestionByPage()` - 分页查询题目
    - `QuestionBizServiceImpl.saveQuestionBizInfoBatch()` - 批量保存题目业务关联

3. **题目统计**:
    - `QuestionServiceImpl.countQuestionNumOfCreater()` - 统计教师出题数量
    - `QuestionBizServiceImpl.queryQuestionScoresByBizIds()` - 查询题目分数

#### 关键类说明

- `QuestionServiceImpl`: 题目服务核心实现，处理题目 CRUD 操作
- `QuestionDetailServiceImpl`: 题目详情服务，管理题目选项和答案
- `QuestionBizServiceImpl`: 题目业务关联服务，管理题目与业务的关联关系
- `QuestionMapper`: 题目数据访问层
- `QuestionType`: 题目类型枚举，定义单选、多选、判断等类型

---

### 9. 消息服务 (tj-message) - 消息通知中心

#### 模块结构

```
tj-message/
├── tj-message-service/     # 消息服务实现
│   ├── controller/         # 控制器层
│   ├── service/            # 业务逻辑层
│   │   ├── impl/
│   │   │   ├── SmsServiceImpl.java      # 短信服务实现
│   │   │   └── NoticeServiceImpl.java   # 通知服务实现
│   │   ├── ISmsService.java
│   │   └── INoticeService.java
│   ├── mapper/             # 数据访问层
│   ├── domain/             # 实体类
│   └── handler/            # 消息处理器
├── tj-message-api/         # API接口定义
└── tj-message-domain/      # 领域模型
```

#### 核心功能实现

##### 消息通知流程

1. **短信发送**:

    - `SmsServiceImpl.sendMessage()` - 发送短信
    - `SmsServiceImpl.sendVerifyCode()` - 发送验证码

2. **通知管理**:

    - `NoticeServiceImpl.sendNotice()` - 发送通知
    - `NoticeServiceImpl.queryNotices()` - 查询通知

3. **消息模板**:
    - 支持多种消息模板
    - 支持消息推送
    - 支持邮件发送

#### 关键类说明

- `SmsServiceImpl`: 短信服务，集成第三方短信服务
- `NoticeServiceImpl`: 通知服务，管理系统通知
- `SmsMapper`: 短信记录数据访问层
- `NoticeMapper`: 通知记录数据访问层

---

### 10. 媒体服务 (tj-media) - 媒体资源中心

#### 模块结构

```
tj-media/
├── controller/      # 控制器层
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── MediaServiceImpl.java      # 媒体服务实现
│   │   └── UploadServiceImpl.java     # 上传服务实现
│   ├── IMediaService.java
│   └── IUploadService.java
├── mapper/          # 数据访问层
├── domain/          # 实体类
└── config/          # 配置类
```

#### 核心功能实现

##### 媒体处理流程

1. **文件上传**:

    - `UploadServiceImpl.uploadFile()` - 文件上传
    - `UploadServiceImpl.uploadImage()` - 图片上传
    - `UploadServiceImpl.uploadVideo()` - 视频上传

2. **媒体处理**:

    - `MediaServiceImpl.processVideo()` - 视频处理
    - `MediaServiceImpl.processImage()` - 图片处理

3. **资源管理**:
    - `MediaServiceImpl.queryMediaList()` - 查询媒体资源
    - `MediaServiceImpl.deleteMedia()` - 删除媒体资源

#### 关键类说明

- `MediaServiceImpl`: 媒体服务，处理媒体资源管理
- `UploadServiceImpl`: 上传服务，处理文件上传
- `MediaMapper`: 媒体资源数据访问层

---

### 11. 搜索服务 (tj-search) - 搜索中心

#### 模块结构

```
tj-search/
├── controller/      # 控制器层
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── SearchServiceImpl.java      # 搜索服务实现
│   │   └── IndexServiceImpl.java       # 索引服务实现
│   ├── ISearchService.java
│   └── IIndexService.java
├── mapper/          # 数据访问层
├── domain/          # 实体类
└── config/          # 配置类
```

#### 核心功能实现

##### 搜索处理流程

1. **索引管理**:

    - `IndexServiceImpl.buildIndex()` - 构建搜索索引
    - `IndexServiceImpl.updateIndex()` - 更新索引

2. **搜索查询**:

    - `SearchServiceImpl.searchCourses()` - 搜索课程
    - `SearchServiceImpl.searchContent()` - 搜索内容

3. **搜索优化**:
    - 支持分词搜索
    - 支持模糊匹配
    - 支持搜索建议

#### 关键类说明

- `SearchServiceImpl`: 搜索服务，处理搜索查询
- `IndexServiceImpl`: 索引服务，管理搜索索引
- `SearchMapper`: 搜索数据访问层

---

### 12. 数据中心 (tj-data) - 数据统计中心

#### 模块结构

```
tj-data/
├── controller/      # 控制器层
├── service/         # 业务逻辑层
│   ├── impl/
│   │   ├── StatisticsServiceImpl.java  # 统计服务实现
│   │   └── ReportServiceImpl.java      # 报表服务实现
│   ├── IStatisticsService.java
│   └── IReportService.java
├── mapper/          # 数据访问层
├── domain/          # 实体类
└── model/           # 数据模型
```

#### 核心功能实现

##### 数据统计流程

1. **数据统计**:

    - `StatisticsServiceImpl.getUserStatistics()` - 用户统计
    - `StatisticsServiceImpl.getCourseStatistics()` - 课程统计
    - `StatisticsServiceImpl.getOrderStatistics()` - 订单统计

2. **报表生成**:

    - `ReportServiceImpl.generateReport()` - 生成报表
    - `ReportServiceImpl.exportReport()` - 导出报表

3. **数据分析**:
    - 支持多维度数据分析
    - 支持趋势分析
    - 支持对比分析

#### 关键类说明

- `StatisticsServiceImpl`: 统计服务，处理各类数据统计
- `ReportServiceImpl`: 报表服务，生成和导出报表
- `StatisticsMapper`: 统计数据访问层

---

### 基础模块

#### 1. 公共模块 (tj-common) - 公共组件中心

#### 模块结构

```
tj-common/
├── autoconfigure/   # 自动配置
├── constants/        # 常量定义
├── domain/          # 领域模型
├── enums/           # 枚举类
├── exceptions/      # 异常处理
├── filters/         # 过滤器
├── utils/           # 工具类
└── validate/        # 验证器
```

#### 核心功能实现

- **工具类**: 提供各种通用工具方法
- **异常处理**: 统一异常处理机制
- **验证器**: 数据验证工具
- **自动配置**: Spring Boot 自动配置
- **常量定义**: 系统常量管理

#### 关键类说明

- `BeanUtils`: Bean 转换工具类
- `AssertUtils`: 断言工具类
- `StringUtils`: 字符串工具类
- `CollUtils`: 集合工具类
- `UserContext`: 用户上下文工具类

#### 2. API 模块 (tj-api) - 接口定义中心

#### 模块结构

```
tj-api/
├── client/          # 客户端接口
│   ├── user/        # 用户服务客户端
│   ├── course/      # 课程服务客户端
│   ├── trade/       # 交易服务客户端
│   └── pay/         # 支付服务客户端
├── dto/             # 数据传输对象
│   ├── user/        # 用户相关DTO
│   ├── course/      # 课程相关DTO
│   ├── trade/       # 交易相关DTO
│   └── pay/         # 支付相关DTO
├── constants/        # 常量定义
└── cache/           # 缓存接口
```

#### 核心功能实现

- **服务间通信**: 定义各服务间的调用接口
- **DTO 定义**: 统一数据传输对象
- **客户端接口**: Feign 客户端接口定义
- **缓存接口**: 缓存操作接口

#### 关键类说明

- `UserClient`: 用户服务客户端接口
- `CourseClient`: 课程服务客户端接口
- `TradeClient`: 交易服务客户端接口
- `PayClient`: 支付服务客户端接口
- `LoginUserDTO`: 登录用户数据传输对象

---

## 业务流程详解

### 用户注册登录完整流程

1. **用户注册**:

   ```
   前端 → Gateway → User服务 → 发送短信 → 验证码校验 → 创建用户 → 返回结果
   ```

2. **用户登录**:

   ```
   前端 → Gateway → Auth服务 → 验证用户 → 生成JWT → 返回Token → 权限验证
   ```

### 课程购买学习完整流程

1. **课程浏览**:

   ```
   前端 → Gateway → Course服务 → 查询课程 → 返回课程列表
   ```

2. **加入购物车**:

   ```
   前端 → Gateway → Trade服务 → 添加购物车 → 返回购物车信息
   ```

3. **下单支付**:

   ```
   前端 → Gateway → Trade服务 → 创建订单 → Pay服务 → 第三方支付 → 支付回调
   ```

4. **学习解锁**:

   ```
   支付成功 → MQ消息 → Learning服务 → 解锁课程 → 开始学习
   ```

### 课程管理完整流程

1. **课程创建**:

   ```
   管理后台 → Gateway → Course服务 → 保存草稿 → 编辑内容 → 上传视频
   ```

2. **课程上架**:

   ```
   管理后台 → Gateway → Course服务 → 上架校验 → 发布课程 → 搜索索引更新
   ```

3. **课程学习**:

   ```
   用户 → Gateway → Learning服务 → 记录学习 → 更新进度 → 生成报告
   ```

### 考试管理完整流程

1. **题目管理**:

   ```
   管理后台 → Gateway → Exam服务 → 创建题目 → 设置答案 → 关联业务
   ```

2. **考试发布**:

   ```
   管理后台 → Gateway → Exam服务 → 组卷 → 发布考试 → 学生答题
   ```

3. **成绩统计**:

   ```
   学生提交 → Exam服务 → 自动阅卷 → 成绩统计 → 生成报告
   ```

---

## 数据库设计

### 主要数据表

- **用户相关**: user, user_detail, role, permission
- **课程相关**: course, course_catalogue, course_teacher, category
- **交易相关**: order, order_detail, pay_order
- **学习相关**: learning_record, learning_lesson
- **考试相关**: question, question_detail, exam, exam_record

## 部署架构

### 开发环境

- **注册中心**: Nacos (192.168.150.101:8848)
- **配置中心**: Nacos Config
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **搜索引擎**: Elasticsearch

### 容器化部署

- **Docker**: 使用 Dockerfile 构建镜像
- **启动脚本**: startup.sh 自动化部署脚本
- **资源配置**: 内存 300m，支持调试模式

## 开发规范

### 代码规范

- 充分使用注释
- 代码解耦，保持可扩展性
- 遵循 RESTful API 设计规范
- 统一异常处理
- 统一返回格式

### 模块规范

- 每个微服务独立部署
- 服务间通过 Feign 进行通信
- 统一使用 Nacos 进行服务注册
- 配置信息统一管理
