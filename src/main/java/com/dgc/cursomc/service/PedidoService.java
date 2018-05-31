package com.dgc.cursomc.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dgc.cursomc.domain.Cliente;
import com.dgc.cursomc.domain.ItemPedido;
import com.dgc.cursomc.domain.PagamentoComBoleto;
import com.dgc.cursomc.domain.Pedido;
import com.dgc.cursomc.domain.Produto;
import com.dgc.cursomc.domain.enums.EstadoPagamento;
import com.dgc.cursomc.exception.AuthorizationException;
import com.dgc.cursomc.exception.ObjectNotFoundException;
import com.dgc.cursomc.repositories.ItemPedidoRepository;
import com.dgc.cursomc.repositories.PagamentoRepository;
import com.dgc.cursomc.repositories.PedidoRepository;
import com.dgc.cursomc.repositories.ProdutoRepository;
import com.dgc.cursomc.security.UserSS;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;

	// @Autowired
	// private ClienteRepository clienteRepository;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private BoletoService boletoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private EmailService emailService;

	public Pedido find(Integer id) {
		Optional<Pedido> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	public Pedido insert2(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		// obj.setCliente(clienteRepository.findOne(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			Optional<Produto> prods = produtoRepository.findById(ip.getProduto().getId());
			Produto prod = prods.orElse(null);
			ip.setProduto(prod);
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());
		// emailService.sendOrderConfirmationEmail(obj);
		return obj;
	}

	@Transactional
	public Pedido insert3(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteService.find(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			Optional<Produto> prods = produtoRepository.findById(ip.getProduto().getId());
			Produto prod = prods.orElse(null);
			ip.setPreco(prod.getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());

		System.out.println(obj);
		return obj;
	}

	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteService.find(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoService.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());
		emailService.sendOrderConfirmationHtmlEmail(obj);
		return obj;
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente =  clienteService.find(user.getId());
		return repo.findByCliente(cliente, pageRequest);
}
}