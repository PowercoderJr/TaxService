package TaxService;

import TaxService.CRUDs.*;
import TaxService.DAO.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class ServerMain
{
	public static void main( String[] args )
	{
		try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory())
		{
			AbstractCRUD<StrangeThing> strangeThingCRUD = new StrangeThingCRUD(sessionFactory);
			AbstractRandomableCRUD<Department> departmentCRUD = new DepartmentCRUD(sessionFactory);
			AbstractRandomableCRUD<Employee> employeeCRUD = new EmployeeCRUD(sessionFactory);
			AbstractRandomableCRUD<Company> companyCRUD = new CompanyCRUD(sessionFactory);
			AbstractRandomableCRUD<Payment> paymentCRUD = new PaymentCRUD(sessionFactory);
			AbstractCRUD<Deptype> deptypeCRUD = new DeptypeCRUD(sessionFactory);
			AbstractCRUD<Education> educationCRUD = new EducationCRUD(sessionFactory);
			AbstractCRUD<Paytype> paytypeCRUD = new PaytypeCRUD(sessionFactory);
			AbstractCRUD<Owntype> owntypeCRUD = new OwntypeCRUD(sessionFactory);

			strangeThingCRUD.create(new StrangeThing("admin", DigestUtils.sha256Hex(DigestUtils.sha256Hex("pass")), StrangeThing.Role.ADMIN));
			String[] deptypes =
					{
							"Районный",
							"Городской",
							"Областной"
					};
			for (String str : deptypes)
				deptypeCRUD.create(new Deptype(str));

			String[] educations =
					{
							"Среднее общее",
							"Среднее профессиональное",
							"Высшее неполное",
							"Высшее базовое",
							"Высшее полное"
					};
			for (String str : educations)
				educationCRUD.create(new Education(str));

			String[] paytypes =
					{
							"Земельный",
							"За использование природных ресурсов",
							"НДС",
							"Акцизы",
							"Экспортные тарифы",
							"На прибыль",
							"На имущество предприятий",
							"На рекламу",
							"Благоустройство территории",
							"Содержание объектов социальной сферы",
							"Нужды образовательных учреждений",
							"Штраф за неуплату налогов",
					};
			for (String str : paytypes)
			paytypeCRUD.create(new Paytype(str));

			String[] owntypes =
					{
							"Государственное унитарное предприятие (ГУП)",
							"Федеральное казенное предприятие (ФКП)",
							"Муниципальное унитарное предприятие (МУУП)",
							"Дочернее унитарное предприятие (ДУП)",
							"Дочернее предприятие (ДП)",
							"Производственный кооператив (ПК)",
							"Артель (Арт)",
							"Сельскохозяйственный кооператив (СХК)",
							"Полное товарищество (ПТ)",
							"Товарищество на вере (ТВ)",
							"Общество с ограниченной ответственностью (ООО)",
							"Общество с дополнительной ответственностью (ОДО)",
							"Открытое акционерное общество (ОАО)",
							"Закрытое акционерное общество (ЗАО)",
							"Филиал (ФЛ)",
							"Представительство (ПРЕД)",
							"Общественная организация (ОО)",
							"Религиозная организация (РО)",
							"Общественное объединение (ООБ)",
							"Религиозное общество (РОБ)",
							"Церковь (Церковь)",
							"Приход (Приход)",
							"Община (Община)",
							"Монастырь (Монастырь)",
							"Миссия (Миссия)",
							"Фонд (Фонд)",
							"Некоммерческое партнерство (НП)",
							"Учреждение (Уч)",
							"Государственное учреждение (ГУЧ)",
							"Муниципальное учреждение (МУЧ)",
							"Общественное учреждение (ОУЧ)",
							"Автономная некоммерческая организация (АНО)",
							"Ассоциация (АС)",
							"Союз (Союз)",
							"Товарищество собственников жилья (ТСЖ)",
							"Кондоминиум (Кондоминиум)",
							"Потребительский кооператив (ПТК)",
							"Общественное движение (ОД)",
							"Общественный фонд (ОФ)",
							"Орган общественной самодеятельности (ООС)",
							"Духовное образовательное учреждение (ДОУч)",
							"Общероссийский профсоюз (Опроф)",
							"Территориальная организация профсоюза (ТОПроф)",
							"Первичная профсоюзная организация (ППО)",
							"Партия (Партия)",
							"Акционерное общество (АО)",
							"Акционерное общество закрытого типа (АОЗТ)",
							"Акционерное общество открытого типа (АООТ)",
							"Товарищество с ограниченной ответственностью (ТОО)",
							"Малое предприятие (МП)",
							"Индивидуальное частное предприятие (ИЧП)",
							"Семейное предприятие (СЕМ)",
							"Крестьянское (фермерское) хозяйство (КФХ)",
							"Крестьянское хозяйство (КХ)",
							"Совместное предприятие (СП)",
							"Государственное предприятие (ГП)",
							"Муниципальное предприятие (МУП)",
							"Предприятие общественной организации (ПОО)",
							"Предприятие потребительской кооперации (ППКООП)",
							"Учреждение общественной организации (УОО)",
							"Учреждение потребительской кооперации (УЧПТК)",
							"Коммерческий банк (Комбанк)",
							"Смешанное товарищество (СМТ)",
							"Садоводческое товарищество (СТ)",
							"Колхоз (КЛХ)",
							"Совхоз (СВХ)",
							"Жилищно-строительный кооператив (ЖСК)",
							"Гаражно-строительный кооператив (ГСК)",
							"Фирма (Фирма)",
							"Научно-производственное объединение (НПО)",
							"Производственное объединение (ПО)",
							"Специализированное конструкторское бюро (СКБ)",
							"Конструкторское бюро (КБ)",
							"Управление производственно-технической комплектации (УПТК)",
							"Строительно-монтажное управление (СМУ)",
							"Хозяйственное управление (ХОЗУ)",
							"Научно-технический центр (НТЦ)",
							"Финансово-инвестиционная компания (ФИК)",
							"Научно-производственное предприятие (НПП)",
							"Чековый инвестиционный фонд (ЧИФ)",
							"Частное охранное предприятие (ЧОП)",
							"Ремонтно-эксплуатационное управление (РЭУ)",
							"Паевой инвестиционный фонд (ПИФ)",
							"Гаражный кооператив (ГКООП)",
							"Потребительское общество (ПОБ)",
							"Потребительский союз (ПС)",
							"Кредитный союз (КС)",
							"Филиал фонда (ФФ)",
							"Финансово-промышленная группа (ФПГ)",
							"Межхозяйственное предприятие (МХП)",
							"Личное подсобное хозяйство (ЛПХ)",
							"Арендное предприятие (АП)",
							"Объединение предприятий (ОП)",
							"Научно-производственная фирма (НПФ)",
							"Производственно-коммерческая фирма (ПКФ)",
							"Производственно-коммерческое предприятие (ПКП)",
							"Производственно-коммерческая компания (ПКК)",
							"Коммерческая фирма (КФ)",
							"Торговая фирма (ТФ)",
							"Торговый дом (ТД)",
							"Дорожное (строительное) управление (Д(С)У)",
							"Транснациональная финансово-промышленная группа (ТФПГ)",
							"Межгосударственная финансово-промышленная группа (МФПГ)",
							"Детский сад (Д/С)",
							"Больница (Б-ца)",
							"Поликлиника (П-ка)",
							"Аптека (А-ка)",
							"Завод (З-д)",
							"Административный округ (АДОК)",
							"Редакция средства массовой информации (РедСМИ)",
							"Простое товарищество (ПрТ)",
							"Арендное предприятие в форме акционерного общества открытого типа АПАООТ)",
							"Арендное предприятие в форме акционерного общества закрытого типа (АПАОЗТ)",
							"Арендное предприятие в форме товарищества с ограниченной ответственностью (АПТОО)",
							"Арендное предприятие в форме смешанного товарищества (АПСТ)",
							"Арендное предприятие в форме полного товарищества (АППТ)",
							"Объединение предприятий в форме акционерного общества открытого типа (ОПАООТ)",
							"Объединение предприятий в форме акционерного общества закрытого типа (ОПАОЗТ)",
							"Объединение предприятий в форме товарищества с ограниченной ответственностью (ОПТОО)",
							"Объединение предприятий в форме смешанного товарищества (ОПСТ)",
							"Объединение предприятий в форме полного товарищества (ОППТ)",
							"Ассоциация крестьянских (фермерских) хозяйств (АСКФХ)",
							"Союз крестьянских (фермерских) хозяйств (СОЮЗКФХ)",
							"Союз потребительских обществ (СОЮЗПОБ)",
							"Школа (Школа)",
							"Институт (Ин-т)",
							"Ремонтно-строительное управление (РСУ)",
							"Корпорация (Корп)",
							"Компания (Комп)",
							"Библиотека (Б-ка)",
							"Больница скорой помощи (БСП)",
							"Центральная районная больница (ЦРБ)",
							"Муниципальное унитарное учреждение (МУУЧ)",
							"Медсанчасть (МСЧ)",
							"Централизованная районная бухгалтерия (ЦРБУХ)",
							"Централизованная бухгалтерия (ЦБУХ)",
							"Финансовый отдел (ФИНОТДЕЛ)",
							"Коммерческий центр (КЦ)",
							"Профсоюзный комитет (ПРОФКОМ)",
							"Автотранспортное предприятие (АТП)",
							"Пассажирское автотранспортное предприятие (ПАТП)",
							"Центр досуга населения (ЦДН)",
							"Нотариальная палата (НОТП)",
							"Нотариальная контора (НОТК)",
							"Ясли-сад (Я/С)",
							"Отделение (ОТД)",
							"Железная дорога (ЖД)",
							"Кооператив (КООП)"
					};
			for (String str : owntypes)
				owntypeCRUD.create(new Owntype(str));

			departmentCRUD.insertRandomBeans(500);
			employeeCRUD.insertRandomBeans(20000);
			companyCRUD.insertRandomBeans(30000);
			paymentCRUD.insertRandomBeans(40000);

			strangeThingCRUD.disconnect();
			departmentCRUD.disconnect();
			employeeCRUD.disconnect();
			companyCRUD.disconnect();
			paymentCRUD.disconnect();
			deptypeCRUD.disconnect();
			educationCRUD.disconnect();
			paytypeCRUD.disconnect();
			owntypeCRUD.disconnect();
		}

		System.out.println("BAC 3AAPEWTOBAHO!");
	}
}
