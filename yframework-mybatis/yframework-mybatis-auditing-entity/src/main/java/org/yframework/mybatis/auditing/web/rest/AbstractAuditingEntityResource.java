package org.yframework.mybatis.auditing.web.rest;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yframework.mybatis.auditing.service.dto.AuditingEntityDTO;
import org.yframework.mybatis.auditing.utils.HeaderUtil;
import org.yframework.mybatis.auditing.utils.PaginationUtil;
import org.yframework.toolkit.y;

import javax.validation.Valid;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Description: AbstractAuditingEntityResource.<br>
 * Date: 2017/9/9 11:57<br>
 * Author: ysj
 */
public abstract class AbstractAuditingEntityResource<VO extends AuditingEntityDTO> implements AuditingEntityResource<VO>
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Class<VO> cls;

    protected final String entityName;

    protected AbstractAuditingEntityResource()
    {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.cls = (Class<VO>) pt.getActualTypeArguments()[0];
        this.entityName = cls.getName();
    }

    @GetMapping
    @Override
    public ResponseEntity<List<VO>> all()
    {
        log.debug("REST request to get all {}", entityName);
        return ResponseEntity.ok(getService().findAll());
    }

    @GetMapping("/filter")
    @Override
    public ResponseEntity<List<VO>> filter(@ApiParam(required = true) VO vo)
    {
        log.debug("REST request to get all {} and filter according to {}", entityName, vo);
        return ResponseEntity.ok(getService().findAll(vo));
    }

    @GetMapping("/{id}")
    @Override
    public <ID extends Serializable> ResponseEntity<VO> get(@PathVariable ID id) throws Exception
    {
        log.debug("REST request to get one {} : {}", entityName, id);
        VO vo = this.cls.newInstance();
        vo.setId(id);
        return ResponseEntity.ok((VO) getService().findOne(vo));
    }

    @GetMapping("/query")
    @Override
    public ResponseEntity<Page<VO>> query(@RequestParam String query, @ApiParam(required = true) Pageable pageable) throws Exception
    {
        log.debug("REST request to search for a page of {} for query {}", entityName, query);
        Page<VO> page = getService().findAll(y.util().json().jsonToObject(query, cls), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page);
    }

    @PostMapping
    @Override
    public ResponseEntity<VO> create(@Valid @RequestBody VO vo) throws Exception
    {
        log.debug("REST request to save {} : {}", entityName, vo);
        if (vo.getId() != null)
        {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(entityName, "idexists", "A new vo cannot already have an ID")).body(null);
        }
        VO result = (VO) getService().save(vo);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert(entityName, result.getId().toString())).body(result);
    }

    @PutMapping
    @Override
    public ResponseEntity<VO> update(@Valid @RequestBody VO vo) throws Exception
    {
        log.debug("REST request to update {} : {}", entityName, vo);
        if (vo.getId() == null)
        {
            return create(vo);
        }
        VO result = (VO) getService().save(vo);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(entityName, result.getId().toString())).body(result);
    }

    @PutMapping("/activate/{id}")
    @Override
    public <ID extends Serializable> ResponseEntity<ID> activate(@PathVariable ID id) throws Exception
    {
        log.debug("REST request to activate {} : {}", entityName, id);
        VO vo = this.cls.newInstance();
        vo.setId(id);
        getService().activate(vo);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/freeze/{id}")
    @Override
    public <ID extends Serializable> ResponseEntity<ID> freeze(@PathVariable ID id) throws Exception
    {
        log.debug("REST request to freeze {} : {}", entityName, id);
        VO vo = this.cls.newInstance();
        vo.setId(id);
        getService().freeze(vo);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @Override
    public <ID extends Serializable> ResponseEntity<ID> delete(@PathVariable ID id) throws Exception
    {
        log.debug("REST request to delete {} : {}", entityName, id);
        VO vo = this.cls.newInstance();
        vo.setId(id);
        getService().delete(vo);
        return ResponseEntity.ok(id);
    }
}
