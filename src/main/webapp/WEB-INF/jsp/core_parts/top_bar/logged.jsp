<%@ page contentType="text/html; charset=UTF-8"%>
<li class="dropdown">
	<a href="#" class="dropdown-toggle" data-toggle="dropdown">
		<i class="fa fa-envelope"></i><b class="caret"></b>
	</a>
	<ul class="dropdown-menu message-dropdown">
		<li class="message-preview">
			<a href="#">
				<span class="media">
					<span class="pull-left"> <img class="media-object"
						src="http://placehold.it/50x50" alt="">
					</span>
					<span class="media-body">
						<span class="media-heading">
							<strong>${account.name}</strong>
						</span>
						<span class="small text-muted">
							<i class="fa fa-clock-o"></i> Yesterday at 4:32 PM
						</span>
						<span>Lorem ipsum dolor sit amet, consectetur...</span>
					</span>
				</span>
			</a>
		</li>
		<li class="message-preview">
			<a href="#">
				<span class="media">
					<span class="pull-left"> <img class="media-object"
						src="http://placehold.it/50x50" alt="">
					</span>
					<span class="media-body">
						<span class="media-heading">
							<strong>${account.name}</strong>
						</span>
						<span class="small text-muted">
							<i class="fa fa-clock-o"></i> Yesterday at 4:32 PM
						</span>
						<span>Lorem ipsum dolor sit amet, consectetur...</span>
					</span>
				</span>
			</a>
		</li>
		<li class="message-preview"><a href="#">
				<span class="media">
					<span class="pull-left"> <img class="media-object"
						src="http://placehold.it/50x50" alt="">
					</span>
					<span class="media-body">
						<span class="media-heading">
							<strong>${account.name}</strong>
						</span>
						<span class="small text-muted">
							<i class="fa fa-clock-o"></i> Yesterday at 4:32 PM
						</span>
						<span>Lorem ipsum dolor sit amet, consectetur...</span>
					</span>
				</span>
		</a></li>
		<li class="message-footer"><a href="#">Read All New Messages</a></li>
	</ul>
</li>

<li class="dropdown">
	<a href="#" class="dropdown-toggle" data-toggle="dropdown">
		<i class="fa fa-bell"></i> <b class="caret"></b>
	</a>
	
	<ul class="dropdown-menu alert-dropdown">
		<li><a href="#">Alert Name <span class="label label-default">Alert Badge</span></a></li>
		<li><a href="#">Alert Name <span class="label label-primary">Alert Badge</span></a></li>
		<li><a href="#">Alert Name <span class="label label-success">Alert Badge</span></a></li>
		<li><a href="#">Alert Name <span class="label label-info">Alert Badge</span></a></li>
		<li><a href="#">Alert Name <span class="label label-warning">Alert Badge</span></a></li>
		<li><a href="#">Alert Name <span class="label label-danger">Alert Badge</span></a></li>
		<li class="divider"></li>
		<li><a href="#">View All</a></li>
	</ul>
</li>

<li class="dropdown">
	<a href="#" class="dropdown-toggle" data-toggle="dropdown">
		<i class="fa fa-user"></i> ${account.name} <b class="caret"></b>
	</a>
	<ul class="dropdown-menu">
		<li><a href="${nav.get('panel')}"><i class="fa fa-fw fa-gear"></i> Panel</a></li>
		<li class="divider"></li>
		<li><a href="${nav.get('logout')}"><i class="fa fa-fw fa-power-off"></i> Wyloguj się</a></li>
	</ul>
</li>