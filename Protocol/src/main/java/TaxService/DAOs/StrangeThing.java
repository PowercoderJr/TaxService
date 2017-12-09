package TaxService.DAOs;

import TaxService.POJO;

import javax.persistence.*;

@Entity
@Table(name = "StrangeThing")
public class StrangeThing extends POJO
{
	public enum Role {ADMIN, USER}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;

	@Column(name = "login", length = 50, unique = true, nullable = false)
	private String login;

	@Column(name = "digest", length = 64, nullable = false)
	private String digest;

	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;

	public StrangeThing()
	{
		;
	}

	public StrangeThing(String login, String digest, Role role)
	{
		this.login = login;
		this.digest = digest;
		this.role = role;
	}

	public long getId()
	{
		return id;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getDigest()
	{
		return digest;
	}

	public void setDigest(String digest)
	{
		this.digest = digest;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	@Override
	public String toString()
	{
		return "StrangeThing{" + "id=" + id + ", login='" + login + '\'' + ", digest='" + digest + '\'' + ", role=" + role + '}';
	}
}
