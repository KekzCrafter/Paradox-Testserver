package de.kekz.testserver.manager.ranks;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.enums.DataInformation;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class RankManager {

	public void setRank(User user, Rank rank, DataInformation info, String value) {
		Rank oldRank = Main.getInstance().getVerifyManager().getRank(info, value);

		Guild guild = Main.getInstance().getBotManager().getGuild();
		Member member = guild.getMember(user);

		guild.removeRoleFromMember(member, guild.getRolesByName(oldRank.getName(), true).get(0)).complete();
		guild.addRoleToMember(member, guild.getRolesByName(rank.getName(), true).get(0)).complete();
		Main.getInstance().getVerifyManager().setRank(info, value, rank);

		user.openPrivateChannel()
				.queue(channel -> channel.sendMessage("Your rank was updated to: " + rank.getName()).queue());
	}
}
