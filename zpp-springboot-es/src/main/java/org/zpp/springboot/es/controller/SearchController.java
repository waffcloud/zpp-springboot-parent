package org.zpp.springboot.es.controller;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zpp.springboot.es.model.CloudDiskEntity;
import org.zpp.springboot.es.reposiory.CloudDiskReposiory;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
	@Autowired
	private CloudDiskReposiory cloudDiskDao;

	@RequestMapping("/search")
	public String search(String keyword, String describe, @PageableDefault(page = 0, value = 5) Pageable pageable,
			HttpServletRequest req) {
		Long startTime = System.currentTimeMillis();
		// 1.创建查询对象
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(keyword)) {
			MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("name", keyword);
			boolQuery.must(matchQuery);
		}
		if (!StringUtils.isEmpty(describe)) {
			MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("describe", describe);
			boolQuery.must(matchQuery);
		}
		// 2.调用查询接口
		Page<CloudDiskEntity> page = cloudDiskDao.search(boolQuery, pageable);

		req.setAttribute("page", page);
		// 记录总数
		req.setAttribute("total", page.getTotalElements());
		req.setAttribute("keyword", keyword);
		// 计算分页总数
		int totalPage = (int) ((page.getTotalElements() - 1) / pageable.getPageSize() + 1);
		// 分页总数
		req.setAttribute("totalPage", totalPage);
		Long emdTime = System.currentTimeMillis();
		req.setAttribute("time", emdTime - startTime);
		return "search";
	}

}