package TaxService.DAOs;

import java.lang.reflect.Field;

public class Account extends AbstractDAO
{
    public enum Roles {JUSTUSER, OPERATOR, ADMIN}

    public String login;
    public String password;
    public Employee employee;
    public Roles role;
    public boolean blocked;

    static
    {
        try
        {
            readEvenIfLazy.put(Account.class, new Field[] {Account.class.getField("id"),
														   Account.class.getField("login")});
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public static final void init(){}

    public Account()
    {
        super();
    }

    public Account(String login, String password, Employee employee, Roles role, boolean blocked)
    {
        this.login = login;
        this.password = password;
        this.employee = employee;
        this.role = role;
        this.blocked = blocked;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Employee getEmployee()
    {
        return employee;
    }

    public void setEmployee(Employee employee)
    {
        this.employee = employee;
    }

    public Roles getRole()
    {
        return role;
    }

    public void setRole(Roles role)
    {
        this.role = role;
    }

    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(boolean blocked)
    {
        this.blocked = blocked;
    }

    @Override
    public String toString()
    {
        return login + " - #" + id;
    }
}
