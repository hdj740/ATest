package com.shirley.aTest.dao;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.shirley.aTest.db.AssertResultRowMapper;
import com.shirley.aTest.entity.AssertResult;

/**
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 371683941@qq.com
 * @date 2019年7月8日 下午2:47:16
 */
@Repository("assertResultDAO")
public class AssertResultDAO implements IAssertResultDAO {
	// 获取JdbcTemplate实例
	@Resource(name = "jdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<AssertResult> QueryAsserts(int currentPageNo, int pageSize, int taskId) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer("select * from asserts where 1=1");
		List<Object> queryList = new ArrayList<Object>();
		if (0 != taskId) {
			sql.append(" and task_id = ?");
			queryList.add(taskId);
		}
		sql.append(" ORDER BY `id` DESC");
		if (currentPageNo != 0 && pageSize != 0) {
			sql.append(" limit ?,?");
			queryList.add((currentPageNo - 1) * pageSize);
			queryList.add(pageSize);
		}
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql.toString(), queryList.toArray());
		List<AssertResult> assertResultList = new ArrayList<AssertResult>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Map<String, Object> row : list) {
			AssertResult assertResult = new AssertResult();
			assertResult.setId((Integer) row.get("id"));
			assertResult.setUrl((String) row.get("url"));
			assertResult.setStatus((String) row.get("status"));
			assertResult.setCreateTime(df.format((Timestamp) row.get("createtime")));
			assertResultList.add(assertResult);
		}
		return assertResultList;
	}

	@Override
	public int QueryAssertsCount(int taskId) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer("select count(id) from asserts where 1=1");
		List<Object> queryList = new ArrayList<Object>();
		if (0 != taskId) {
			sql.append(" and task_id = ?");
			queryList.add(taskId);
		}

		return this.jdbcTemplate.queryForObject(sql.toString(), queryList.toArray(), Integer.class);
	}

	@Override
	public AssertResult QueryAssert(int assertId) {
		// TODO Auto-generated method stub
		String sql = "select * from asserts where id=?";
		return this.jdbcTemplate.queryForObject(sql, new AssertResultRowMapper(), assertId);
	}

	@Override
	public Boolean AddAsserts(AssertResult assertResult) {
		// TODO Auto-generated method stub
		String sql = "insert into asserts (task_id,url,requestcontent,responsecontent,assertresult,status) values (?,?,?,?,?,?)";
		int row = this.jdbcTemplate.update(sql, new Object[] { assertResult.getTaskId(), assertResult.getUrl(),
				assertResult.getRequestContent().length() > 5000 ? assertResult.getRequestContent().substring(0, 5000)
						: assertResult.getRequestContent(),
				assertResult.getResponseContent().length() > 5000 ? assertResult.getResponseContent().substring(0, 5000)
						: assertResult.getResponseContent(),
				assertResult.getAssertResult(), assertResult.getStatus() });
		return row > 0;

	}

}
