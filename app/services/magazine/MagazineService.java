package services.magazine;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.magazine.MagazineInfo;
import models.magazine.MagazineUser;
import models.magazine.Magazinelist;
import play.Logger;
import utils.Constants;

public class MagazineService {
	private static final Logger.ALogger logger = Logger.of(MagazineService.class);
	
	/**
	 * 根据magid获取magid对应的详情列表
	 * 
	 * @param magid
	 * @return
	 */
	public static List<MagazineInfo> getMagazineImg(int magid) {
		List<MagazineInfo> magazineinfoImgList = Ebean.getServer(Constants.getDB()).find(MagazineInfo.class).where()
				.eq("magid", magid).findList();
		return magazineinfoImgList;
	}

	/**
	 * 根据id获取杂志列表的详情
	 * 
	 * @param id
	 * @return
	 */
	public static Magazinelist getMagazinelistById(Long id) {
		if(id==null){
			id=0L;
		}
		return Ebean.getServer(Constants.getDB()).find(Magazinelist.class, id);
	}
	/**
	 * 保存用户与杂志的关联信息
	 * @param magazineUser
	 * @return
	 */
	public static MagazineUser saveMagazineUser(MagazineUser magazineUser) {
		Ebean.getServer(Constants.getDB()).save(magazineUser);
		return magazineUser;
	}
	/**
	 * 根据杂志ID与用户ID获取是否收藏过此杂志
	 * @param magid
	 * @param uid
	 * @return
	 */
	public static MagazineUser getMagaZineUserByMagidAndUid(Integer magid,Integer uid) {
		List<MagazineUser> magazineUserList = Ebean.getServer(Constants.getDB()).find(MagazineUser.class).where().eq("magid", magid).eq("uid", uid).findList();
		if(magazineUserList.isEmpty()){
			return null;
		}else{
			return magazineUserList.get(0);
		}
	}
	
}