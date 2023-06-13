package de.kekz.testserver.manager.bot.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberJoinListener extends ListenerAdapter {

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		Member member = e.getMember();
		e.getGuild().addRoleToMember(member, e.getGuild().getRolesByName("User", true).get(0)).complete();
	}
}
