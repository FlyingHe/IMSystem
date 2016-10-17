package at.flying.domain;

/**
 * Created by FlyingHe on 2016/9/21.
 */
/*
    此类用于标识客户端和服务端相互发送信息时，指定该信息的类型，
    以使后端或者前端根据信息类型执行不同的操作
*/
public class MsgType {
    /*更新在线列表*/
    public static final String UPDATE_TALKERS_ONLINE = "updateTalkersOnline";
    /*用户下线*/
    public static final String LOGOFF_TALKER = "logoffTalker";
    /*用户发送消息*/
    public static final String SEND_MESSAGE = "sendMessage";
    /*用户发送信息给机器人*/
    public static final String SEND_MESSAGE_TO_Robot = "sendMessageToRobot";
    /*用户发送消息成功*/
    public static final String SEND_MESSAGE_SUCCESSFUL = "sendMessageSuccessful";
}
