package cn.luorenmu.command.entity

/**
 * @author LoMu
 * Date 2024.11.17 19:52
 */
enum class BotRole(val role: String, val roleNumber: Int) {
    OWNER("owner", 10),
    ADMIN("admin", 5),
    GroupOwner("group_owner", 4),
    GroupAdmin("group_admin", 3),
    Member("member", 0);

   companion object{
       fun convert(role: String): BotRole {
           return entries.first { it.role == role }
       }
   }
}