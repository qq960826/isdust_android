package pw.isdust.isdust.function;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import pw.isdust.isdust.Http;

/**
 * Created by wzq on 15/10/19.
 */
public class Library {
    Http mHttp;
    String [] mPersonalInformation;
    public Library(){
        mHttp=new Http();
    }
    public String login(String user,String password){
        String msubmit="rdid="+user+"&rdPasswd="+md5(password)+"&returnUrl=";
        String text= mHttp.post_string("http://interlib.sdust.edu.cn/opac/reader/doLogin",msubmit);
        if (text.contains("读者姓名")){
            Pattern mPattern=Pattern.compile("<font class=\"space_font\">([\\S\\s]*?)</font>");
            Matcher mMatcher=mPattern.matcher(text);
            List<String> array_temp=new ArrayList<String>();
            while (mMatcher.find()){
                mMatcher.start();
                array_temp.add(mMatcher.group(1));
                mMatcher.end();
            }
            int len=array_temp.size();
            mPersonalInformation=new String[len];
            for (int i=0;i<len;i++){
                mPersonalInformation[i]=array_temp.get(i).toString();

            }


            return "登录成功";
        }
        return "账号或密码错误";

    }
    public String get_name(){
        return mPersonalInformation[1];
    }
    public String [] [] get_borrwingdetail(){
        String text=mHttp.get_string("http://interlib.sdust.edu.cn/opac/loan/renewList");
        Pattern mPattern=Pattern.compile("<td width=\"40\"><input type=\"checkbox\" name=\"barcodeList\" value=\"([0-9]*?)\" />[\\s\\S]*?target=\"_blank\">([\\S\\s]*?)</a></td>[\\S\\s]*?<td width=\"100\">([0-9]{4}-[0-9]{2}-[0-9]{2})[\\S\\s]*?<td width=\"100\">([0-9]{4}-[0-9]{2}-[0-9]{2})");
        Matcher mMatcher=mPattern.matcher(text);
        List<String []> array_temp=new ArrayList<String []>();
        while(mMatcher.find()){
            String [] temp= new String[4];
            mMatcher.start();
            for (int j=0;j<4;j++){
                temp[j]=mMatcher.group(j+1);
            }
            array_temp.add(temp);

        }
        int len=array_temp.size();
        String [] [] result=new String[len][4];
        for (int i=0;i<len;i++){
            String [] temp=array_temp.get(i);
            result[i]=temp;
        }
        return result;
    }
    public Book [] xml_analyze_jianjie(String text) throws ParserConfigurationException, IOException, SAXException {
//        String text=mHttp.get_string("http://interlib.sdust.edu.cn/opac/book/holdingpreview/248193");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(text));

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(is);
        NodeList nodes = document.getElementsByTagName("record");
        int len=nodes.getLength();
        Book result []=new Book[len];
        String  []c=new String[6];
        for (int i=0;i<len;i++){
            for (int j=0;j<6;j++){
                c[j]=nodes.item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue();
            }
            Book temp=new Book(c[0],c[2],c[4],c[5]);
            result[i]=temp;
        }


        return result;
    }
    public void json_analyze(){
        String text=mHttp.get_string("http://interlib.sdust.edu.cn/opac/api/holding/1900737876");
        try {
            JSONArray mJSONArray=new JSONArray(text);
            int len=mJSONArray.length();
        }catch (Exception e){
            System.out.println(e);

        }

    }
    public String renew_all(){
        String text=mHttp.post_string("http://interlib.sdust.edu.cn/opac/loan/doRenew","furl=%2Fopac%2Floan%2FrenewList&renewAll=all" );
        String content=Networklogin_CMCC.zhongjian(text, "<div style=\"margin:20px auto; width:50%; height:auto!important; min-height:200px; border:2px dashed #ccc;\">", "<input", 0);
        content=content.replace("\t","");
        content=content.replace("\n","");
        content=content.replace("\r", "");
        String [] temp_array=content.split("<br/>");
        int len=temp_array.length-2;
        String result="";
        for(int i=0;i<len;i++){
            result=result+temp_array[i+1]+"\n";
        }
        return result;

    }
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";}
    public
    class Book{
        String mFindingNumber;//索书号
        String mLibryLocation;//所在馆
        String mSpecificLocation;//所在馆藏地点
        String mCopy;//在馆复本数
        public Book(String FindingNumber, String LibryLocation,String SpecificLocation,String Copy){
            mFindingNumber=FindingNumber;
            mLibryLocation=LibryLocation;
            mSpecificLocation=SpecificLocation;
            mCopy=Copy;
        }
        public String get_FindingNumber(){
            return  mFindingNumber;
        }
        public String get_LibryLocation(){
            return  mLibryLocation;
        }
        public String get_SpecificLocation(){
            return  mSpecificLocation;
        }
        public String get_Copy(){
            return  mCopy;
        }
    }



    public static String getlocalMap(String text){//所在馆位置
        JSONObject mJSONObject;
        try {
            mJSONObject=new JSONObject("{\"QBCK\":\"青岛科图版书库\",\"JNJC\":\"济南教材参考库\",\"QXKYL\":\"青岛现刊阅览室\",\"QZKK\":\"青岛自科书库\",\"QWYK\":\"青岛外文书样本库\",\"QWWK\":\"青岛外文书库\",\"TDZKK\":\"泰东自科现刊\",\"QZYK\":\"青岛中文书样本库\",\"TDYB\":\"泰东样本库\",\"KJGK\":\"泰西过刊\",\"JNGK\":\"中文期刊\",\"JNWWK\":\"济南外文刊\",\"TDZT\":\"泰东中图库\",\"TDSKK\":\"泰东社科现刊\",\"JNGP\":\"济南随书光盘库\",\"QSKK\":\"青岛社科书库\",\"JNQK\":\"济南期刊库\",\"JNSK\":\"济南社科借阅区\",\"QJCK\":\"青岛教材样本库\",\"TDKT\":\"泰东科图库\",\"KJZKK\":\"泰西自科现刊\",\"QMJK\":\"青岛密集库\",\"QDZY\":\"青岛电子阅览室\",\"TDGK\":\"泰东过刊库\",\"TDXS\":\"泰东学生阅览室\",\"JNGJ\":\"济南工具书\",\"TDKY\":\"泰东考研库\",\"QGKK\":\"青岛过期期刊库\",\"Q007\":\"青岛未分配流通库\",\"JNXS\":\"济南学生借书处\",\"JNBC\":\"济南保存库\",\"TDWW\":\"泰东外文库\",\"TDZH\":\"泰东综合库\",\"KJTC\":\"特藏图书\",\"JNZK\":\"济南自科借阅区\",\"JNFY\":\"济南复印\",\"TDTC\":\"泰东特藏库\",\"KJZT\":\"泰西中图库\",\"TDZLS\":\"泰文法资料室\",\"JNWW\":\"济南外文借书处\",\"WFFG\":\"文法分馆\",\"QGJK\":\"青岛工具书库\",\"JNLS\":\"济南临时库\",\"QTCK\":\"青岛特藏书库\",\"KJSKK\":\"泰西社科现刊\",\"JNJS\":\"济南教师借书处\",\"JNDZ\":\"济南电子阅览室\",\"TDJS\":\"泰东教师阅览室\"}");//所在馆位置
            return mJSONObject.get(text).toString();
        }catch (Exception e){

        }
        return "";


    }
    public static String getorglib(String text){//文献所属馆
        JSONObject mJSONObject;
        try {
            mJSONObject=new JSONObject("{\"02000\":\"泰安东校区\",\"04000\":\"泰山科技学院\",\"01000\":\"青岛校区\",\"01000 \":null,\"03000\":\"济南校区\",\"999\":\"山东科技大学图书馆\",\"05000\":\"文法分馆\"}");//所在馆位置
            return mJSONObject.get(text).toString();
        }catch (Exception e){

        }
        return "";


    }
    public static String getstate(String text){//馆藏状态
        JSONObject mJSONObject;
        try {
            mJSONObject=new JSONObject("{\"0\":{\"stateType\":0,\"stateName\":\"流通还回上架中\"},\"1\":{\"stateType\":1,\"stateName\":\"编目\"},\"2\":{\"stateType\":2,\"stateName\":\"在馆\"},\"3\":{\"stateType\":3,\"stateName\":\"借出\"},\"4\":{\"stateType\":4,\"stateName\":\"丢失\"},\"5\":{\"stateType\":5,\"stateName\":\"剔除\"},\"6\":{\"stateType\":6,\"stateName\":\"交换\"},\"7\":{\"stateType\":7,\"stateName\":\"赠送\"},\"8\":{\"stateType\":8,\"stateName\":\"装订\"},\"9\":{\"stateType\":9,\"stateName\":\"锁定\"},\"10\":{\"stateType\":10,\"stateName\":\"预借\"},\"12\":{\"stateType\":12,\"stateName\":\"清点\"},\"13\":{\"stateType\":13,\"stateName\":\"闭架\"},\"14\":{\"stateType\":14,\"stateName\":\"修补\"},\"15\":{\"stateType\":15,\"stateName\":\"查找中\"}}");//馆藏状态
            return mJSONObject.getJSONObject(text).getString("stateName");
        }catch (Exception e){

        }
        return "";


    }
    public static void getBook(String text){
        JSONArray mJSONArray;

        String raw_tushuxinxi="[{\""+Networklogin_CMCC.zhongjian(text,"[{\"","}]",0)+"}]";
        String raw_jieyuexinxi=Networklogin_CMCC.zhongjian(text,"{\"loanWorkMap\":",",\"holdingList",0);
        List<String []> mfinal=new ArrayList<String []>();
        try {
            mJSONArray=new JSONArray(raw_tushuxinxi);//馆藏状态
            int len=mJSONArray.length();
            for (int i=0;i<len;i++){
                String result []=new String[6];
                result[0]=mJSONArray.getJSONObject(i).getString("callno");//索书号
                result[1]=mJSONArray.getJSONObject(i).getString("barcode");//条码号
                result[2]=getstate(mJSONArray.getJSONObject(i).getString("state"));//馆藏状态
                if (result[2].equals("借出")){
                    result[3]=TimeStamp2Date(getreturndate(raw_jieyuexinxi,result[1]));
                }else {
                    result[3]="";
                }
                //result[3]=mJSONArray.getJSONObject(i).getString("barcode");//条码号
                result[4] = getorglib(mJSONArray.getJSONObject(i).getString("orglib"));//文献所属馆
                result[5] = getlocalMap(mJSONArray.getJSONObject(i).getString("orglocal"));//所在馆位置
                mfinal.add(result);
            }
        }catch (Exception e){

        }
    }
    public static String getreturndate(String raw,String barcode){
        JSONObject mJSONObject;

        try {
            mJSONObject=new JSONObject(raw);//馆藏状态
            return mJSONObject.getJSONObject(barcode).getString("returnDate");

        }catch (Exception e){
            System.out.println(e);

        }
        return "";
    }
    public static String TimeStamp2Date(String timestampString){
        Long timestamp = Long.parseLong(timestampString);
        String date = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date(timestamp));
        return date;
    }


}
